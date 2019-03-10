package manon.api.app;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import manon.service.app.InfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.Globals.API.API_SYS;
import static manon.util.Tools.Media.TEXT;

@Api(description = "Information about running application. Used by: admin, except application statud that is public.")
@RestController
@RequestMapping(value = API_SYS)
@RequiredArgsConstructor
public class InfoWS {
    
    private final InfoService infoService;
    
    @ApiOperation(value = "Get application version.", produces = TEXT)
    @GetMapping(value = "/info/app-version")
    public String getAppVersion() {
        return infoService.getAppVersion();
    }
    
    @ApiOperation(value = "Get application status 'UP' if running, otherwise an HTTP error. This endpoint is public.", produces = TEXT)
    @GetMapping(value = "/info/up")
    public String getUp() {
        return "UP";
    }
}
