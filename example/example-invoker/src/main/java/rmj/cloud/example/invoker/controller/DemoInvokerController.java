package rmj.cloud.example.invoker.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import rmj.cloud.example.common.entity.DemoEntity;
import rmj.cloud.common.util.ResultObject;
import rmj.cloud.example.invoker.service.IDemoService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/invoker", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api("Invoker api")
public class DemoInvokerController {

    @Autowired
    private IDemoService demoService;

    @Autowired
    private RestTemplate restTemplate;

    @ApiOperation(value = "分布式调用", notes = "分布式调用")
    @GetMapping("outerService")
    public ResultObject<List<DemoEntity>> outerService() {
        return restTemplate.getForObject("http://example-service/api/demoEntity/all", ResultObject.class);
    }

}
