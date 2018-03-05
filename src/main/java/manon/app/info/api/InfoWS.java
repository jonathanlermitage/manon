package manon.app.info.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.info.service.InfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.config.API.API_SYS;

@RestController
@RequestMapping(value = API_SYS)
@RequiredArgsConstructor
@Slf4j
public class InfoWS {
    
    private final InfoService infoService;
    
    @GetMapping(value = "/info/app-version")
    public String getAppVersion() {
        return infoService.getAppVersion();
    }
}
