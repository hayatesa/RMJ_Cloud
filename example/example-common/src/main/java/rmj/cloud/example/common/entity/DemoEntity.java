package rmj.cloud.example.common.entity;

import javax.persistence.*;

@Entity
@Table(name = "demo_entity")
public class DemoEntity extends BaseEntity {

    @Column(name = "d_name", columnDefinition = "varchar(64) COMMENT '名称'")
    private String dName;

    @Column(name = "num", columnDefinition = "int COMMENT 'int字段'")
    private Integer num;

    @Column(name = "status", columnDefinition = "tinyint COMMENT 'byte字段'")
    private Byte status;

    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

}
