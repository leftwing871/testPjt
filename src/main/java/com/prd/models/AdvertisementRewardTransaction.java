package com.prd.models;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.json.JSONObject;

import com.google.gson.JsonObject;

//import io.searchbox.annotations.JestId;


public class AdvertisementRewardTransaction {

	static SimpleDateFormat dt1 = new SimpleDateFormat("yyyyMMdd");
	static SimpleDateFormat dt2 = new SimpleDateFormat("HH");

	//@JestId
	private String id;
	private Short HH;
	
	private Integer YYYYMMDD;

	private short adType;
	
	private Date createdAt;
	
	private short leagueClass;
	
	private short level;
	
	private String note;
	
	private long owner;
	
	private String nation;

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}	
	
	public Short getHH() {
		return HH;
	}

	public void setHH(Short hH) {
		HH = hH;
	}

	public Integer getYYYYMMDD() {
		return YYYYMMDD;
	}

	public void setYYYYMMDD(Integer yYYYMMDD) {
		YYYYMMDD = yYYYMMDD;
	}

	public short getAdType() {
		return adType;
	}

	public void setAdType(short adType) {
		this.adType = adType;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public short getLeagueClass() {
		return leagueClass;
	}

	public void setLeagueClass(short leagueClass) {
		this.leagueClass = leagueClass;
	}

	public short getLevel() {
		return level;
	}

	public void setLevel(short level) {
		this.level = level;
	}

	public JSONObject getNote() {
		return new JSONObject(note);
	}

	public void setNote(JSONObject note) {
		this.note = note.toString();
	}

	public long getOwner() {
		return owner;
	}

	public void setOwner(long owner) {
		this.owner = owner;
	}
	
	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public AdvertisementRewardTransaction() {
	}
	
	public LocalDateTime getCreatedAtLocalDate() {
		return LocalDateTime.ofInstant(createdAt.toInstant(), ZoneId.of("Asia/Seoul"));
	}
	
	public AdvertisementRewardTransaction(long owner, short level, short adType, short leagueClass, String nation, String noteStr) {
		this.owner = owner;
		this.level = level;
		this.adType = adType;
		this.leagueClass = leagueClass;
		this.createdAt = Date.from(LocalDateTime.now().plusHours(9).atZone(ZoneId.systemDefault()).toInstant());
		this.YYYYMMDD = Integer.parseInt(dt1.format(this.createdAt));
		this.HH = Short.parseShort(dt2.format(this.createdAt));
		this.note = noteStr;
		this.nation = nation;
	}
	
	public String getIndexName() {
		return "advertisement_reward_log";
	}

}
