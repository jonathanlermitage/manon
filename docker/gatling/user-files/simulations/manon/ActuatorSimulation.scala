package manon

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

/**
 * A simple scenario that reads server's Actuator Health Check as guest user then admin.
 */
class ActuatorSimulation extends Simulation {

    val httpProtocol = http
        .baseUrl("http://nginx:80")
        .acceptHeader("*/*")

    val scn = scenario("simple scenario")

        // Health Check as guest user
        .exec(http("request_health_asGuest")
            .get("/actuator/health")
            .check(substring(""""status":"UP"""").find.exists)
        )

        // authenticate as admin user
        .exec(http("request_authorize_root") // Here's an example of a POST request
            .post("/api/v1/user/auth/authorize")
            .header("Content-Type", "application/json")
            .body(StringBody("""{"username": "ROOT", "password":"woot"}"""))
            .check(status.is(200))
            .check(bodyString.saveAs("JWT_TOKEN"))
        ).exitHereIfFailed

        // Health Check as authenticated admin user
        .exec(http("request_health_asRoot")
            .get("/actuator/health")
            .header("Authorization", "Bearer ${JWT_TOKEN}")
        )

        // Health Check as admin user
        .exec(http("request_listUsers_asRoot")
            .get("/api/v1/admin/user/all?offset=0&size=100&sort=creationDate,DESC")
            .header("Authorization", "Bearer ${JWT_TOKEN}")
        )

    setUp(
        scn.inject(rampUsers(20) during (10 seconds))
    ).protocols(httpProtocol)
}
