package rmj.cloud.provider.service;


import rmj.cloud.provider.domain.DemoEntity;

import java.util.List;

public interface IDemoService {

    List<DemoEntity> findByName(String name);

    void deleteById(String id);

    void findById(String id);

    void save(DemoEntity entity);

    List<DemoEntity> findAll();

    DemoEntity updateStatusById(String id);

}
