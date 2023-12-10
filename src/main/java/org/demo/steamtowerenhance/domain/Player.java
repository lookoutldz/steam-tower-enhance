package org.demo.steamtowerenhance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * @TableName player
 */
@TableName(value ="player")
public class Player implements Serializable {
    private Integer id;

    private String steamid;

    private Integer communityvisibilitystate;

    private Integer profilestate;

    private String personaname;

    private Integer lastlogoff;

    private Integer commentpermission;

    private String profileurl;

    private String avatar;

    private String avatarmedium;

    private String avatarfull;

    private String avatarhash;

    private Integer personastate;

    private String realname;

    private String primaryclanid;

    private Integer timecreated;

    private Integer personastateflags;

    private String gameextrainfo;

    private String gameserverip;

    private Integer gameid;

    private String loccountrycode;

    private String locstatecode;

    private String loccityid;

    private Integer steamlevel;

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

    public Integer getCommunityvisibilitystate() {
        return communityvisibilitystate;
    }

    public void setCommunityvisibilitystate(Integer communityvisibilitystate) {
        this.communityvisibilitystate = communityvisibilitystate;
    }

    public Integer getProfilestate() {
        return profilestate;
    }

    public void setProfilestate(Integer profilestate) {
        this.profilestate = profilestate;
    }

    public String getPersonaname() {
        return personaname;
    }

    public void setPersonaname(String personaname) {
        this.personaname = personaname;
    }

    public Integer getLastlogoff() {
        return lastlogoff;
    }

    public void setLastlogoff(Integer lastlogoff) {
        this.lastlogoff = lastlogoff;
    }

    public Integer getCommentpermission() {
        return commentpermission;
    }

    public void setCommentpermission(Integer commentpermission) {
        this.commentpermission = commentpermission;
    }

    public String getProfileurl() {
        return profileurl;
    }

    public void setProfileurl(String profileurl) {
        this.profileurl = profileurl;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatarmedium() {
        return avatarmedium;
    }

    public void setAvatarmedium(String avatarmedium) {
        this.avatarmedium = avatarmedium;
    }

    public String getAvatarfull() {
        return avatarfull;
    }

    public void setAvatarfull(String avatarfull) {
        this.avatarfull = avatarfull;
    }

    public String getAvatarhash() {
        return avatarhash;
    }

    public void setAvatarhash(String avatarhash) {
        this.avatarhash = avatarhash;
    }

    public Integer getPersonastate() {
        return personastate;
    }

    public void setPersonastate(Integer personastate) {
        this.personastate = personastate;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getPrimaryclanid() {
        return primaryclanid;
    }

    public void setPrimaryclanid(String primaryclanid) {
        this.primaryclanid = primaryclanid;
    }

    public Integer getTimecreated() {
        return timecreated;
    }

    public void setTimecreated(Integer timecreated) {
        this.timecreated = timecreated;
    }

    public Integer getPersonastateflags() {
        return personastateflags;
    }

    public void setPersonastateflags(Integer personastateflags) {
        this.personastateflags = personastateflags;
    }

    public String getGameextrainfo() {
        return gameextrainfo;
    }

    public void setGameextrainfo(String gameextrainfo) {
        this.gameextrainfo = gameextrainfo;
    }

    public String getGameserverip() {
        return gameserverip;
    }

    public void setGameserverip(String gameserverip) {
        this.gameserverip = gameserverip;
    }

    public Integer getGameid() {
        return gameid;
    }

    public void setGameid(Integer gameid) {
        this.gameid = gameid;
    }

    public String getLoccountrycode() {
        return loccountrycode;
    }

    public void setLoccountrycode(String loccountrycode) {
        this.loccountrycode = loccountrycode;
    }

    public String getLocstatecode() {
        return locstatecode;
    }

    public void setLocstatecode(String locstatecode) {
        this.locstatecode = locstatecode;
    }

    public String getLoccityid() {
        return loccityid;
    }

    public void setLoccityid(String loccityid) {
        this.loccityid = loccityid;
    }

    public Integer getSteamlevel() {
        return steamlevel;
    }

    public void setSteamlevel(Integer steamlevel) {
        this.steamlevel = steamlevel;
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
        Player other = (Player) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSteamid() == null ? other.getSteamid() == null : this.getSteamid().equals(other.getSteamid()))
            && (this.getCommunityvisibilitystate() == null ? other.getCommunityvisibilitystate() == null : this.getCommunityvisibilitystate().equals(other.getCommunityvisibilitystate()))
            && (this.getProfilestate() == null ? other.getProfilestate() == null : this.getProfilestate().equals(other.getProfilestate()))
            && (this.getPersonaname() == null ? other.getPersonaname() == null : this.getPersonaname().equals(other.getPersonaname()))
            && (this.getLastlogoff() == null ? other.getLastlogoff() == null : this.getLastlogoff().equals(other.getLastlogoff()))
            && (this.getCommentpermission() == null ? other.getCommentpermission() == null : this.getCommentpermission().equals(other.getCommentpermission()))
            && (this.getProfileurl() == null ? other.getProfileurl() == null : this.getProfileurl().equals(other.getProfileurl()))
            && (this.getAvatar() == null ? other.getAvatar() == null : this.getAvatar().equals(other.getAvatar()))
            && (this.getAvatarmedium() == null ? other.getAvatarmedium() == null : this.getAvatarmedium().equals(other.getAvatarmedium()))
            && (this.getAvatarfull() == null ? other.getAvatarfull() == null : this.getAvatarfull().equals(other.getAvatarfull()))
            && (this.getAvatarhash() == null ? other.getAvatarhash() == null : this.getAvatarhash().equals(other.getAvatarhash()))
            && (this.getPersonastate() == null ? other.getPersonastate() == null : this.getPersonastate().equals(other.getPersonastate()))
            && (this.getRealname() == null ? other.getRealname() == null : this.getRealname().equals(other.getRealname()))
            && (this.getPrimaryclanid() == null ? other.getPrimaryclanid() == null : this.getPrimaryclanid().equals(other.getPrimaryclanid()))
            && (this.getTimecreated() == null ? other.getTimecreated() == null : this.getTimecreated().equals(other.getTimecreated()))
            && (this.getPersonastateflags() == null ? other.getPersonastateflags() == null : this.getPersonastateflags().equals(other.getPersonastateflags()))
            && (this.getGameextrainfo() == null ? other.getGameextrainfo() == null : this.getGameextrainfo().equals(other.getGameextrainfo()))
            && (this.getGameserverip() == null ? other.getGameserverip() == null : this.getGameserverip().equals(other.getGameserverip()))
            && (this.getGameid() == null ? other.getGameid() == null : this.getGameid().equals(other.getGameid()))
            && (this.getLoccountrycode() == null ? other.getLoccountrycode() == null : this.getLoccountrycode().equals(other.getLoccountrycode()))
            && (this.getLocstatecode() == null ? other.getLocstatecode() == null : this.getLocstatecode().equals(other.getLocstatecode()))
            && (this.getLoccityid() == null ? other.getLoccityid() == null : this.getLoccityid().equals(other.getLoccityid()))
            && (this.getSteamlevel() == null ? other.getSteamlevel() == null : this.getSteamlevel().equals(other.getSteamlevel()))
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
        result = prime * result + ((getCommunityvisibilitystate() == null) ? 0 : getCommunityvisibilitystate().hashCode());
        result = prime * result + ((getProfilestate() == null) ? 0 : getProfilestate().hashCode());
        result = prime * result + ((getPersonaname() == null) ? 0 : getPersonaname().hashCode());
        result = prime * result + ((getLastlogoff() == null) ? 0 : getLastlogoff().hashCode());
        result = prime * result + ((getCommentpermission() == null) ? 0 : getCommentpermission().hashCode());
        result = prime * result + ((getProfileurl() == null) ? 0 : getProfileurl().hashCode());
        result = prime * result + ((getAvatar() == null) ? 0 : getAvatar().hashCode());
        result = prime * result + ((getAvatarmedium() == null) ? 0 : getAvatarmedium().hashCode());
        result = prime * result + ((getAvatarfull() == null) ? 0 : getAvatarfull().hashCode());
        result = prime * result + ((getAvatarhash() == null) ? 0 : getAvatarhash().hashCode());
        result = prime * result + ((getPersonastate() == null) ? 0 : getPersonastate().hashCode());
        result = prime * result + ((getRealname() == null) ? 0 : getRealname().hashCode());
        result = prime * result + ((getPrimaryclanid() == null) ? 0 : getPrimaryclanid().hashCode());
        result = prime * result + ((getTimecreated() == null) ? 0 : getTimecreated().hashCode());
        result = prime * result + ((getPersonastateflags() == null) ? 0 : getPersonastateflags().hashCode());
        result = prime * result + ((getGameextrainfo() == null) ? 0 : getGameextrainfo().hashCode());
        result = prime * result + ((getGameserverip() == null) ? 0 : getGameserverip().hashCode());
        result = prime * result + ((getGameid() == null) ? 0 : getGameid().hashCode());
        result = prime * result + ((getLoccountrycode() == null) ? 0 : getLoccountrycode().hashCode());
        result = prime * result + ((getLocstatecode() == null) ? 0 : getLocstatecode().hashCode());
        result = prime * result + ((getLoccityid() == null) ? 0 : getLoccityid().hashCode());
        result = prime * result + ((getSteamlevel() == null) ? 0 : getSteamlevel().hashCode());
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
        sb.append(", communityvisibilitystate=").append(communityvisibilitystate);
        sb.append(", profilestate=").append(profilestate);
        sb.append(", personaname=").append(personaname);
        sb.append(", lastlogoff=").append(lastlogoff);
        sb.append(", commentpermission=").append(commentpermission);
        sb.append(", profileurl=").append(profileurl);
        sb.append(", avatar=").append(avatar);
        sb.append(", avatarmedium=").append(avatarmedium);
        sb.append(", avatarfull=").append(avatarfull);
        sb.append(", avatarhash=").append(avatarhash);
        sb.append(", personastate=").append(personastate);
        sb.append(", realname=").append(realname);
        sb.append(", primaryclanid=").append(primaryclanid);
        sb.append(", timecreated=").append(timecreated);
        sb.append(", personastateflags=").append(personastateflags);
        sb.append(", gameextrainfo=").append(gameextrainfo);
        sb.append(", gameserverip=").append(gameserverip);
        sb.append(", gameid=").append(gameid);
        sb.append(", loccountrycode=").append(loccountrycode);
        sb.append(", locstatecode=").append(locstatecode);
        sb.append(", loccityid=").append(loccityid);
        sb.append(", steamlevel=").append(steamlevel);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", deleteTime=").append(deleteTime);
        sb.append(", deleted=").append(deleted);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}