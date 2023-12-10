package org.demo.steamtowerenhance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName app
 */
@TableName(value ="app")
public class App implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @JsonProperty("appid")
    private Integer appid;
    @JsonProperty("name")
    private String appname;
    private String chname;
    private String imgIconUrl;
    private String imgLogoUrl;
    private String screenshotUrl;
    private Date updateTime;
    private Date createTime;
    private Date deleteTime;
    private Integer deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getAppid() {
        return appid;
    }
    public void setAppid(Integer appid) {
        this.appid = appid;
    }
    public String getAppname() {
        return appname;
    }
    public void setAppname(String appname) {
        this.appname = appname;
    }
    public String getChname() {
        return chname;
    }
    public void setChname(String chname) {
        this.chname = chname;
    }
    public String getImgIconUrl() {
        return imgIconUrl;
    }
    public void setImgIconUrl(String imgIconUrl) {
        this.imgIconUrl = imgIconUrl;
    }
    public String getImgLogoUrl() {
        return imgLogoUrl;
    }
    public void setImgLogoUrl(String imgLogoUrl) {
        this.imgLogoUrl = imgLogoUrl;
    }
    public String getScreenshotUrl() {
        return screenshotUrl;
    }
    public void setScreenshotUrl(String screenshotUrl) {
        this.screenshotUrl = screenshotUrl;
    }
    public Date getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Date getDeleteTime() {
        return deleteTime;
    }
    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }
    public Integer getDeleted() {
        return deleted;
    }
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        App other = (App) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getAppid() == null ? other.getAppid() == null : this.getAppid().equals(other.getAppid()))
            && (this.getAppname() == null ? other.getAppname() == null : this.getAppname().equals(other.getAppname()))
            && (this.getChname() == null ? other.getChname() == null : this.getChname().equals(other.getChname()))
            && (this.getImgIconUrl() == null ? other.getImgIconUrl() == null : this.getImgIconUrl().equals(other.getImgIconUrl()))
            && (this.getImgLogoUrl() == null ? other.getImgLogoUrl() == null : this.getImgLogoUrl().equals(other.getImgLogoUrl()))
            && (this.getScreenshotUrl() == null ? other.getScreenshotUrl() == null : this.getScreenshotUrl().equals(other.getScreenshotUrl()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getDeleteTime() == null ? other.getDeleteTime() == null : this.getDeleteTime().equals(other.getDeleteTime()))
            && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getAppid() == null) ? 0 : getAppid().hashCode());
        result = prime * result + ((getAppname() == null) ? 0 : getAppname().hashCode());
        result = prime * result + ((getChname() == null) ? 0 : getChname().hashCode());
        result = prime * result + ((getImgIconUrl() == null) ? 0 : getImgIconUrl().hashCode());
        result = prime * result + ((getImgLogoUrl() == null) ? 0 : getImgLogoUrl().hashCode());
        result = prime * result + ((getScreenshotUrl() == null) ? 0 : getScreenshotUrl().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getDeleteTime() == null) ? 0 : getDeleteTime().hashCode());
        result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", appid=").append(appid);
        sb.append(", appname=").append(appname);
        sb.append(", chname=").append(chname);
        sb.append(", imgIconUrl=").append(imgIconUrl);
        sb.append(", imgLogoUrl=").append(imgLogoUrl);
        sb.append(", screenshotUrl=").append(screenshotUrl);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", deleteTime=").append(deleteTime);
        sb.append(", deleted=").append(deleted);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}