package manon.util;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import manon.util.basetest.AbstractParallelTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.plantuml.rules.PlantUmlArchCondition.Configuration.consideringOnlyDependenciesInDiagram;
import static com.tngtech.archunit.library.plantuml.rules.PlantUmlArchCondition.adhereToPlantUmlDiagram;
import static org.assertj.core.api.Assertions.assertThat;

class ArchTest extends AbstractParallelTest {

    private static final JavaClasses PROJECT = new ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages("manon..");

    private static final ArchCondition<JavaClass> NOT_CALL_METHOD_THAT_EXISTS_FOR_TESTS =
        new ArchCondition<>("not call methods that exist for tests") {
            @Override
            public void check(@NotNull JavaClass item, ConditionEvents events) {
                item.getMethodCallsFromSelf().stream()
                    .filter(method -> !method.getOrigin().isAnnotatedWith(ExistForTesting.class))
                    .filter(method -> method.getTarget().isAnnotatedWith(ExistForTesting.class))
                    .forEach(method -> {
                        String message = String.format("Method %s annotated with ExistForTesting is called from %s",
                            method.getTarget().getFullName(),
                            method.getDescription());
                        events.add(SimpleConditionEvent.violated(method, message));
                    });
            }
        };

    private static final ArchCondition<JavaClass> NOT_CALL_OTHER_SERVICE_REPOSITORY =
        new ArchCondition<>("service should not depend on other services repositories (fooService should depend on fooRepository, not barRepository)") {
            @Override
            public void check(@NotNull JavaClass item, ConditionEvents events) {
                if (item.isAnnotatedWith(Service.class)) {
                    item.getFields().stream()
                        .map(javaField -> javaField.getRawType().getSimpleName())
                        .filter(fieldClassSimpleName -> fieldClassSimpleName.endsWith("Repository") &&
                            !fieldClassSimpleName.equals(item.getSimpleName().substring(0, item.getSimpleName().indexOf("Service")) + "Repository"))
                        .forEach(className -> {
                            String message = String.format("Class %s depends on %s",
                                item.getSimpleName(),
                                className);
                            events.add(SimpleConditionEvent.violated(className, message));
                        });
                }
            }
        };

    private static final ArchCondition<JavaClass> NOT_HAVE_METHODS_RETURNING_ENTITIES =
        new ArchCondition<>("controllers methods should not return entities") {
            @Override
            public void check(@NotNull JavaClass item, ConditionEvents events) {
                item.getMethods()
                    .forEach(method -> {
                        String typeName = method.reflect().getGenericReturnType().getTypeName();
                        if (typeName.contains("manon.document.")) {
                            String message = String.format("Class %s's method %s returns an entity of type %s",
                                item.getSimpleName(),
                                method.getName(),
                                typeName);
                            events.add(SimpleConditionEvent.violated(method, message));
                        }
                    });
            }
        };

    @Test
    void shouldNotDependOnJDKInternals() {
        classes()
            .should().onlyAccessClassesThat().resideOutsideOfPackages("com.sun..", "sun..")
            .check(PROJECT);
    }

    @Test
    void shouldVerifyProductionCodeDontUseMethodThatExistsForTests() {
        classes()
            .should(NOT_CALL_METHOD_THAT_EXISTS_FOR_TESTS)
            .check(PROJECT);
    }

    @Test
    void shouldVerifyLayeredArchitecture() {
        // a real-life project would also define layers like Entity and Dto, then ensure strong architecture
        layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("API").definedBy("manon.api..")
            .layer("Batch").definedBy("manon.batch..")
            .layer("Config").definedBy("manon.app.config..", "manon")
            .layer("Repository").definedBy("manon.repository..")
            .layer("Service").definedBy("manon.service..")
            .whereLayer("API").mayNotBeAccessedByAnyLayer()
            .whereLayer("Batch").mayNotBeAccessedByAnyLayer()
            .whereLayer("Config").mayNotBeAccessedByAnyLayer()
            .whereLayer("Repository").mayOnlyBeAccessedByLayers("Batch", "Service")
            .whereLayer("Service").mayOnlyBeAccessedByLayers("API", "Batch", "Config")
            .check(PROJECT);
    }

    @Test
    void shouldVerifyLayeredArchitectureWithPUML() {
        URL diag = ArchTest.class.getResource("../../expected/layers.puml");
        assertThat(diag).isNotNull();
        classes()
            .should(adhereToPlantUmlDiagram(diag, consideringOnlyDependenciesInDiagram()))
            .check(PROJECT);
    }

    @Test
    void shouldVerifyServiceDependsOnItsOwnRepository() {
        classes().that()
            .areAnnotatedWith(Service.class)
            .should(NOT_CALL_OTHER_SERVICE_REPOSITORY)
            .check(PROJECT);
    }

    @Test
    void shouldVerifyControllerArch() {
        classes().that()
            .haveSimpleNameEndingWith("WS")
            .should().beAnnotatedWith(RestController.class)
            .andShould().beAnnotatedWith(RequestMapping.class)
            .andShould().notBeInterfaces()
            .andShould().resideInAPackage("..api..")
            .check(PROJECT);
    }

    @Test
    void shouldVerifyControllersDontReturnEntities() {
        classes().that()
            .haveSimpleNameEndingWith("WS")
            .should(NOT_HAVE_METHODS_RETURNING_ENTITIES)
            .check(PROJECT);
    }

    @Test
    void shouldVerifyRepositoryArch() {
        classes().that()
            .haveSimpleNameEndingWith("Repository")
            .should().beAnnotatedWith(Repository.class)
            .andShould().beInterfaces()
            .andShould().resideInAPackage("..repository..")
            .check(PROJECT);
    }

    @Test
    void shouldVerifyRepositoryAreUsedByServicesOnly() {
        classes().that()
            .areAnnotatedWith(Repository.class)
            .should().onlyBeAccessed().byClassesThat().areAnnotatedWith(Service.class)
            .check(PROJECT);
    }

    @Test
    @Disabled("there is no RepositoryCustom classes yet") // TODO activate arch-test if added RepositoryCustom classes
    void shouldVerifyRepositoryCustomArch() {
        classes().that()
            .haveSimpleNameEndingWith("RepositoryCustom")
            .should().notBeAnnotatedWith(Repository.class)
            .andShould().resideInAPackage("..repository..")
            .andShould().beInterfaces()
            .check(PROJECT);
    }

    @Test
    @Disabled("there is no RepositoryImpl classes yet") // TODO activate arch-test if added RepositoryImpl classes
    void shouldVerifyRepositoryImplArch() {
        classes().that()
            .haveSimpleNameEndingWith("RepositoryImpl")
            .should().beAnnotatedWith(Repository.class)
            .andShould().notBeInterfaces()
            .andShould().resideInAPackage("..repository..")
            .check(PROJECT);
    }

    @Test
    void shouldVerifyServiceArch() {
        classes().that()
            .haveSimpleNameEndingWith("Service")
            .should().notBeAnnotatedWith(Service.class)
            .andShould().beInterfaces()
            .andShould().resideInAPackage("..service..")
            .check(PROJECT);
    }

    @Test
    void shouldVerifyServicesImplArch() {
        classes().that()
            .haveSimpleNameEndingWith("ServiceImpl")
            .should().beAnnotatedWith(Service.class)
            .andShould().notBeInterfaces()
            .andShould().resideInAPackage("..service..")
            .check(PROJECT);
    }
}
