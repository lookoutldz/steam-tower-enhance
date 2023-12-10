package org.demo.steamtowerenhance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName friend
 */
@TableName(value ="friend")
public class Friend implements Serializable {
    private Integer id;

    private String steamid;

    private String friendsteamid;

    private String relationship;

    @JsonProperty("friend_since")
    private Integer friendSince;

    private Date updateTime;

    private Date createTime;

    private Date deleteTime;

    private Integer deleted;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSteamid() {
        return steamid;
    }

    public void setSteamid(String steamid) {
        this.steamid = steamid;
    }

    public String getFriendsteamid() {
        return friendsteamid;
    }

    public void setFriendsteamid(String friendsteamid) {
        this.friendsteamid = friendsteamid;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public Integer getFriendSince() {
        return friendSince;
    }

    public void setFriendSince(Integer friendSince) {
        this.friendSince = friendSince;
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
        Friend other = (Friend) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSteamid() == null ? other.getSteamid() == null : this.getSteamid().equals(other.getSteamid()))
            && (this.getFriendsteamid() == null ? other.getFriendsteamid() == null : this.getFriendsteamid().equals(other.getFriendsteamid()))
            && (this.getRelationship() == null ? other.getRelationship() == null : this.getRelationship().equals(other.getRelationship()))
            && (this.getFriendSince() == null ? other.getFriendSince() == null : this.getFriendSince().equals(other.getFriendSince()))
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
        result = prime * result + ((getSteamid() == null) ? 0 : getSteamid().hashCode());
        result = prime * result + ((getFriendsteamid() == null) ? 0 : getFriendsteamid().hashCode());
        result = prime * result + ((getRelationship() == null) ? 0 : getRelationship().hashCode());
        result = prime * result + ((getFriendSince() == null) ? 0 : getFriendSince().hashCode());
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
        sb.append(", steamid=").append(steamid);
        sb.append(", friendsteamid=").append(friendsteamid);
        sb.append(", relationship=").append(relationship);
        sb.append(", friendSince=").append(friendSince);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", deleteTime=").append(deleteTime);
        sb.append(", deleted=").append(deleted);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}