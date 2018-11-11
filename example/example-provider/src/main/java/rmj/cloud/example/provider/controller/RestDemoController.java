package rmj.cloud.example.provider.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import rmj.cloud.provider.util.ResultObject;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api("RestDemo api")
public class RestDemoController {

    private static final Logger logger = LoggerFactory.getLogger(RestDemoController.class);

    @ApiOperation(value = "get 请求", notes = "测试get请求")
    @ApiImplicitParam(name = "param", value = "参数")
    @GetMapping("getDemo")
    public ResultObject doGet(Integer param) {
        logger.info("getDemo: " + param);
        return ResultObject.success();
    }

    @ApiOperation(value = "post 请求", notes = "测试post请求")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "param1", value = "参数1"),
            @ApiImplicitParam(name = "param2", value = "参数2")
    })
    @PostMapping("postDemo")
    public ResultObject doPost(@RequestParam Integer param1,
                               @RequestParam Integer param2) {
        logger.info("getDemo: " + param1 + " " + param2);
        return ResultObject.success();
    }

    @ApiIgnore
    @GetMapping("ingnore")
    public ResultObject doIgnore() {
        return ResultObject.success();
    }
}
