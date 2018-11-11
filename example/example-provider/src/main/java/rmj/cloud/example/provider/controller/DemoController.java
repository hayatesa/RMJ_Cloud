package rmj.cloud.example.provider.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rmj.cloud.example.common.entity.DemoEntity;
import rmj.cloud.common.util.ResultObject;
import rmj.cloud.example.provider.service.IDemoService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "/api/demoEntity", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api("JPA Demo api")
public class DemoController {

    @Autowired
    private IDemoService demoService;

    public void setDemoService(IDemoService demoService) {
        this.demoService = demoService;
    }

    @ApiOperation(value = "根据名称查找实体", notes = "根据名称查找实体")
    @ApiImplicitParam(name = "name", value = "名称")
    @GetMapping("findByName")
    public ResultObject<List<DemoEntity>> findByName(@RequestParam("name") String name) {
        return ResultObject.success(demoService.findByName(name));
    }

    @ApiOperation(value = "添加实体", notes = "添加实体")
    @ApiImplicitParam(name = "name", value = "名称")
    @GetMapping("add")
    public ResultObject add() {
        return ResultObject.success();
    }

    @ApiOperation(value = "保存实体", notes = "保存实体")
    @ApiImplicitParam(name = "id", value = "ID", required = false)
    @GetMapping("save")
    public ResultObject save(String id) {
        DemoEntity e = new DemoEntity();
        if (!StringUtils.isEmpty(id)) {
            e.setId(id);
        }
        e.setdName("add" + new Date().getTime());
        e.setStatus((byte) 1);
        e.setNum(1);
        e.setDeleted(false);
        demoService.save(e);
        return ResultObject.success();
    }

    @ApiOperation(value = "查询所有", notes = "查询所有")
    @GetMapping("all")
    public ResultObject findAll() {
        return ResultObject.success(demoService.findAll());
    }

    @ApiOperation(value = "Set Status to 2", notes = "Set Status to 2")
    @ApiImplicitParam(name = "id", value = "ID")
    @GetMapping("status2")
    public ResultObject updateStatusById(String id) {
        return ResultObject.success(demoService.findAll());
    }

}
