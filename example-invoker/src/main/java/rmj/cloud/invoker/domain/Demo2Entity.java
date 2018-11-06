package rmj.cloud.invoker.domain;

@Entity
@Table(name = "demo2_entity")
public class Demo2Entity extends BaseEntity {

    @JoinColumn(name = "demo1_id", updatable = false, insertable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private DemoEntity demoEntity;

}
