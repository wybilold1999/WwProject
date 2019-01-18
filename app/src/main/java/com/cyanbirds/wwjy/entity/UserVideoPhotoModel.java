package com.cyanbirds.wwjy.entity;

import java.util.List;

public class UserVideoPhotoModel implements java.io.Serializable {

	public Integer id;
	public String userId;
	public String nickName;
	public String age;
	public String faceUrl;
	public String occupation;
	public List<String> photoUrl;//用户的相册
	public List<VideoModel> videos;

}
