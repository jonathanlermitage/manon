package manon.app.sys.api;

import lombok.RequiredArgsConstructor;
import manon.app.sys.service.InfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.config.Globals.API.API_SYS;

@RestController
@RequestMapping(value = API_SYS)
@RequiredArgsConstructor
public class InfoWS {
    
    private final InfoService infoService;
    
    @GetMapping(value = "/info/app-version")
    public String getAppVersion() {
        return infoService.getAppVersion();
    }
    
    @GetMapping(value = "/info/up")
    public String getUp() {
        return "UP";
    }
}
