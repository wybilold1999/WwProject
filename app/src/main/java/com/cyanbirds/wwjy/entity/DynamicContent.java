package com.cyanbirds.wwjy.entity;


import java.io.Serializable;
import java.util.List;

/**
 * @author: wangyb
 * @datetime: 2016-02-20 14:07 GMT+8
 * @email: 395044952@qq.com
 * @description:
 */
public class DynamicContent implements Serializable{


	/**
	 * code : 0
	 * msg :
	 * data : [{"id":1,"usersId":1,"faceUrl":"","nickname":"LOL","createTime":"1467191356447","content":null,"pictures":[{"id":1,"usersId":2,"dynamicId":1,"createTime":"1467191356447","size":null,"width":null,"height":null,"path":"http://real-love-server.img-cn-shenzhen.aliyuncs.com/double_love/dl_ce8603c3-7609-4ccc-917e-be5bdb17fb27","form":null},{"id":2,"usersId":2,"dynamicId":1,"createTime":"1467191356447","size":null,"width":null,"height":null,"path":"http://real-love-server.img-cn-shenzhen.aliyuncs.com/double_love/dl_2cefa2ce-333a-4dc5-b9a0-c49102c1c739","form":null},{"id":3,"usersId":2,"dynamicId":1,"createTime":"1467191356447","size":null,"width":null,"height":null,"path":"http://real-love-server.img-cn-shenzhen.aliyuncs.com/double_love/dl_2cefa2ce-333a-4dc5-b9a0-c49102c1c739","form":""},{"id":4,"usersId":2,"dynamicId":1,"createTime":"1467191356447","size":null,"width":null,"height":null,"path":"http://real-love-server.img-cn-shenzhen.aliyuncs.com/double_love/dl_2cefa2ce-333a-4dc5-b9a0-c49102c1c739","form":""},{"id":5,"usersId":2,"dynamicId":1,"createTime":"1467191356447","size":null,"width":null,"height":null,"path":"http://real-love-server.img-cn-shenzhen.aliyuncs.com/double_love/dl_2cefa2ce-333a-4dc5-b9a0-c49102c1c739","form":""},{"id":6,"usersId":2,"dynamicId":1,"createTime":"1467191356447","size":null,"width":null,"height":null,"path":"http://real-love-server.img-cn-shenzhen.aliyuncs.com/double_love/dl_2cefa2ce-333a-4dc5-b9a0-c49102c1c739","form":""},{"id":7,"usersId":2,"dynamicId":1,"createTime":"1467191356447","size":null,"width":null,"height":null,"path":"http://real-love-server.img-cn-shenzhen.aliyuncs.com/double_love/dl_2cefa2ce-333a-4dc5-b9a0-c49102c1c739","form":""},{"id":8,"usersId":2,"dynamicId":1,"createTime":"1467191356447","size":null,"width":null,"height":null,"path":"http://real-love-server.img-cn-shenzhen.aliyuncs.com/double_love/dl_2cefa2ce-333a-4dc5-b9a0-c49102c1c739","form":""}]}]
	 */

	private int code;
	/**
	 * id : 1
	 * usersId : 1
	 * faceUrl :
	 * nickname : LOL
	 * createTime : 1467191356447
	 * content : null
	 * pictures : [{"id":1,"usersId":2,"dynamicId":1,"createTime":"1467191356447","size":null,"width":null,"height":null,"path":"http://real-love-server.img-cn-shenzhen.aliyuncs.com/double_love/dl_ce8603c3-7609-4ccc-917e-be5bdb17fb27","form":null},{"id":2,"usersId":2,"dynamicId":1,"createTime":"1467191356447","size":null,"width":null,"height":null,"path":"http://real-love-server.img-cn-shenzhen.aliyuncs.com/double_love/dl_2cefa2ce-333a-4dc5-b9a0-c49102c1c739","form":null},{"id":3,"usersId":2,"dynamicId":1,"createTime":"1467191356447","size":null,"width":null,"height":null,"path":"http://real-love-server.img-cn-shenzhen.aliyuncs.com/double_love/dl_2cefa2ce-333a-4dc5-b9a0-c49102c1c739","form":""},{"id":4,"usersId":2,"dynamicId":1,"createTime":"1467191356447","size":null,"width":null,"height":null,"path":"http://real-love-server.img-cn-shenzhen.aliyuncs.com/double_love/dl_2cefa2ce-333a-4dc5-b9a0-c49102c1c739","form":""},{"id":5,"usersId":2,"dynamicId":1,"createTime":"1467191356447","size":null,"width":null,"height":null,"path":"http://real-love-server.img-cn-shenzhen.aliyuncs.com/double_love/dl_2cefa2ce-333a-4dc5-b9a0-c49102c1c739","form":""},{"id":6,"usersId":2,"dynamicId":1,"createTime":"1467191356447","size":null,"width":null,"height":null,"path":"http://real-love-server.img-cn-shenzhen.aliyuncs.com/double_love/dl_2cefa2ce-333a-4dc5-b9a0-c49102c1c739","form":""},{"id":7,"usersId":2,"dynamicId":1,"createTime":"1467191356447","size":null,"width":null,"height":null,"path":"http://real-love-server.img-cn-shenzhen.aliyuncs.com/double_love/dl_2cefa2ce-333a-4dc5-b9a0-c49102c1c739","form":""},{"id":8,"usersId":2,"dynamicId":1,"createTime":"1467191356447","size":null,"width":null,"height":null,"path":"http://real-love-server.img-cn-shenzhen.aliyuncs.com/double_love/dl_2cefa2ce-333a-4dc5-b9a0-c49102c1c739","form":""}]
	 */

	private List<DataBean> data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public List<DataBean> getData() {
		return data;
	}

	public void setData(List<DataBean> data) {
		this.data = data;
	}

	public static class DataBean {
		private int id;
		private int usersId;
		private String faceUrl;
		private String nickname;
		private String createTime;
		private String content;
		private int count;
		/**
		 * id : 1
		 * usersId : 2
		 * dynamicId : 1
		 * createTime : 1467191356447
		 * size : null
		 * width : null
		 * height : null
		 * path : http://real-love-server.img-cn-shenzhen.aliyuncs.com/double_love/dl_ce8603c3-7609-4ccc-917e-be5bdb17fb27
		 * form : null
		 */

		private List<PicturesBean> pictures;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getUsersId() {
			return usersId;
		}

		public void setUsersId(int usersId) {
			this.usersId = usersId;
		}

		public String getFaceUrl() {
			return faceUrl;
		}

		public void setFaceUrl(String faceUrl) {
			this.faceUrl = faceUrl;
		}

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		public String getCreateTime() {
			return createTime;
		}

		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public List<PicturesBean> getPictures() {
			return pictures;
		}

		public void setPictures(List<PicturesBean> pictures) {
			this.pictures = pictures;
		}

		public static class PicturesBean {
			private int usersId;
			private int dynamicId;
			private String createTime;
			private String path;

			public int getUsersId() {
				return usersId;
			}

			public void setUsersId(int usersId) {
				this.usersId = usersId;
			}

			public int getDynamicId() {
				return dynamicId;
			}

			public void setDynamicId(int dynamicId) {
				this.dynamicId = dynamicId;
			}

			public String getCreateTime() {
				return createTime;
			}

			public void setCreateTime(String createTime) {
				this.createTime = createTime;
			}

			public String getPath() {
				return path;
			}

			public void setPath(String path) {
				this.path = path;
			}
		}
	}
}
