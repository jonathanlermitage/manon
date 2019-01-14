package manon.util;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.testng.annotations.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class ArchTest {
    
    private final String[] allPackages = new String[]{"manon.."};
    private final String[] controlerPackages = new String[]{"manon.app.batch.api", "manon.app.info.api", "manon.user.api"};
    private final String[] repositoryPackages = new String[]{"manon.app.trace.repository", "manon.user.repository"};
    private final String[] servicePackages = new String[]{"manon.app.trace.service", "manon.user.service"};
    
    @Test
    public void shouldNotDependOnJDKInternals() {
        JavaClasses classes = new ClassFileImporter().importPackages(allPackages);
        ArchRule rules = classes()
            .should().onlyAccessClassesThat().resideOutsideOfPackage("com.sun..");
        rules.check(classes);
    }
    
    @Test
    public void shouldVerifyControlerArch() {
        JavaClasses classes = new ClassFileImporter().importPackages(controlerPackages);
        ArchRule rules = classes().that()
            .haveSimpleNameEndingWith("WS")
            .should().beAnnotatedWith(RestController.class)
            .andShould().beAnnotatedWith(RequestMapping.class)
            .andShould().notBeInterfaces();
        rules.check(classes);
    }
    
    @Test
    public void shouldVerifyRepositoryArch() {
        JavaClasses classes = new ClassFileImporter().importPackages(repositoryPackages);
        ArchRule rules = classes().that()
            .haveSimpleNameEndingWith("Repository")
            .should().beAnnotatedWith(Repository.class)
            .andShould().beInterfaces();
        rules.check(classes);
    }
    
    @Test
    public void shouldVerifyRepositoryCustomArch() {
        JavaClasses classes = new ClassFileImporter().importPackages(repositoryPackages);
        ArchRule rules = classes().that()
            .haveSimpleNameEndingWith("RepositoryCustom")
            .should().notBeAnnotatedWith(Repository.class)
            .andShould().beInterfaces();
        rules.check(classes);
    }
    
    @Test
    public void shouldVerifyRepositoryImplArch() {
        JavaClasses classes = new ClassFileImporter().importPackages(repositoryPackages);
        ArchRule rules = classes().that()
            .haveSimpleNameEndingWith("RepositoryImpl")
            .should().beAnnotatedWith(Repository.class)
            .andShould().notBeInterfaces();
        rules.check(classes);
    }
    
    @Test
    public void shouldVerifyServiceArch() {
        JavaClasses classes = new ClassFileImporter().importPackages(servicePackages);
        ArchRule rules = classes().that()
            .haveSimpleNameEndingWith("Service")
            .should().notBeAnnotatedWith(Service.class)
            .andShould().beInterfaces();
        rules.check(classes);
    }
    
    @Test
    public void shouldVerifyServicesImplArch() {
        JavaClasses classes = new ClassFileImporter().importPackages(servicePackages);
        ArchRule rules = classes().that()
            .haveSimpleNameEndingWith("ServiceImpl")
            .should().beAnnotatedWith(Service.class)
            .andShould().notBeInterfaces();
        rules.check(classes);
    }
}
