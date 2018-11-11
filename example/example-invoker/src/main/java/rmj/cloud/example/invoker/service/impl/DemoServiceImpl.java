package rmj.cloud.example.invoker.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rmj.cloud.invoker.domain.DemoEntity;
import rmj.cloud.example.invoker.repository.DemoRepository;
import rmj.cloud.example.invoker.service.IDemoService;

import java.util.List;

@Service
@Transactional
public class DemoServiceImpl implements IDemoService {

    @Autowired
    private DemoRepository demoRepository;

    public void setDemoRepository(DemoRepository demoRepository) {
        this.demoRepository = demoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemoEntity> findByName(String name) {
        return demoRepository.findByName(name);
    }

    @Override
    public void save(DemoEntity entity) {
        demoRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemoEntity> findAll() {
        return demoRepository.findAll();
    }

    @Override
    public DemoEntity updateStatusById(String id) {
        demoRepository.updateStatusById(id);
        return demoRepository.findById(id).get();
    }

    @Override
    public void deleteById(String id) {
        demoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public void findById(String id) {
        demoRepository.findById(id);
    }
}
