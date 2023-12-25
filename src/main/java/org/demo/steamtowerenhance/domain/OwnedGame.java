package org.demo.steamtowerenhance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * @TableName owned_game
 */
@TableName(value ="owned_game")
public class OwnedGame implements DatabaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String steamid;

    private Integer appid;

    @JsonProperty("name")
    private String appname;

    @JsonProperty("playtime_2weeks")
    private Integer playtime2weeks;

    @JsonProperty("playtime_forever")
    private Integer playtimeForever;

    @JsonProperty("img_icon_url")
    private String imgIconUrl;

    @JsonProperty("img_logo_url")
    private String imgLogoUrl;

    @JsonProperty("has_community_visible_state")
    private Integer hasCommunityVisibleState;

    @JsonProperty("has_leaderboards")
    private Boolean hasLeaderboards;

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

    public Integer getPlaytime2weeks() {
        return playtime2weeks;
    }

    public void setPlaytime2weeks(Integer playtime2weeks) {
        this.playtime2weeks = playtime2weeks;
    }

    public Integer getPlaytimeForever() {
        return playtimeForever;
    }

    public void setPlaytimeForever(Integer playtimeForever) {
        this.playtimeForever = playtimeForever;
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

    public Integer getHasCommunityVisibleState() {
        return hasCommunityVisibleState;
    }

    public void setHasCommunityVisibleState(Integer hasCommunityVisibleState) {
        this.hasCommunityVisibleState = hasCommunityVisibleState;
    }

    public Boolean getHasLeaderboards() {
        return hasLeaderboards;
    }

    public void setHasLeaderboards(Boolean hasLeaderboards) {
        this.hasLeaderboards = hasLeaderboards;
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
        OwnedGame other = (OwnedGame) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSteamid() == null ? other.getSteamid() == null : this.getSteamid().equals(other.getSteamid()))
            && (this.getAppid() == null ? other.getAppid() == null : this.getAppid().equals(other.getAppid()))
            && (this.getAppname() == null ? other.getAppname() == null : this.getAppname().equals(other.getAppname()))
            && (this.getPlaytime2weeks() == null ? other.getPlaytime2weeks() == null : this.getPlaytime2weeks().equals(other.getPlaytime2weeks()))
            && (this.getPlaytimeForever() == null ? other.getPlaytimeForever() == null : this.getPlaytimeForever().equals(other.getPlaytimeForever()))
            && (this.getImgIconUrl() == null ? other.getImgIconUrl() == null : this.getImgIconUrl().equals(other.getImgIconUrl()))
            && (this.getImgLogoUrl() == null ? other.getImgLogoUrl() == null : this.getImgLogoUrl().equals(other.getImgLogoUrl()))
            && (this.getHasCommunityVisibleState() == null ? other.getHasCommunityVisibleState() == null : this.getHasCommunityVisibleState().equals(other.getHasCommunityVisibleState()))
            && (this.getHasLeaderboards() == null ? other.getHasLeaderboards() == null : this.getHasLeaderboards().equals(other.getHasLeaderboards()))
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
        result = prime * result + ((getAppid() == null) ? 0 : getAppid().hashCode());
        result = prime * result + ((getAppname() == null) ? 0 : getAppname().hashCode());
        result = prime * result + ((getPlaytime2weeks() == null) ? 0 : getPlaytime2weeks().hashCode());
        result = prime * result + ((getPlaytimeForever() == null) ? 0 : getPlaytimeForever().hashCode());
        result = prime * result + ((getImgIconUrl() == null) ? 0 : getImgIconUrl().hashCode());
        result = prime * result + ((getImgLogoUrl() == null) ? 0 : getImgLogoUrl().hashCode());
        result = prime * result + ((getHasCommunityVisibleState() == null) ? 0 : getHasCommunityVisibleState().hashCode());
        result = prime * result + ((getHasLeaderboards() == null) ? 0 : getHasLeaderboards().hashCode());
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
        sb.append(", appid=").append(appid);
        sb.append(", appname=").append(appname);
        sb.append(", playtime2week=").append(playtime2weeks);
        sb.append(", playtimeForever=").append(playtimeForever);
        sb.append(", imgIconUrl=").append(imgIconUrl);
        sb.append(", imgLogoUrl=").append(imgLogoUrl);
        sb.append(", hasCommunityVisibleState=").append(hasCommunityVisibleState);
        sb.append(", hasLeaderboards=").append(hasLeaderboards);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", deleteTime=").append(deleteTime);
        sb.append(", deleted=").append(deleted);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}