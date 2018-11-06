package rmj.cloud.provider.domain;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 实体编号（唯一标识）
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id", length = 32, columnDefinition="varchar(32) COMMENT '唯一标识'")
    protected String id;

    /**
     * 备注
     */
    @Column(name = "remarks", length = 255, columnDefinition="varchar(255) COMMENT '备注'")
    protected String remarks;

    /**
     * 创建者
     */
    @CreatedBy
    @Column(name = "create_by", length = 64, columnDefinition="varchar(64) COMMENT '创建者'")
    protected String createBy;

    /**
     * 创建时间
     */
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "gmt_created", columnDefinition="datetime COMMENT '创建时间'")
    protected Date gmtCreated;

    /**
     * 更新者
     */
    @LastModifiedBy
    @Column(name = "update_by", length = 64, columnDefinition="varchar(64) COMMENT '更新者'")
    protected String updateBy;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "gmt_modified", columnDefinition="datetime COMMENT '更新时间'")
    protected Date gmtModified;

    /**
     * 删除标识（0：未删除；1：已删除；）
     */
    @Column(name = "id_deleted", columnDefinition="bit(1) COMMENT '删除标识（0：未删除；1：已删除；）'")
    protected boolean isDeleted;

    /**
     * 数据版本号（更新或删除数据时使用乐观锁机制）
     */
    @Version
    @Column(name = "version", columnDefinition="int(11) COMMENT '数据版本号（更新或删除数据时使用乐观锁机制）'")
    protected int version;

    @Transient
    public boolean isNew() {
        return this.id == null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
