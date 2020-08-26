package bih.in.tarkariapp.entity;


import com.google.gson.annotations.SerializedName;

public class LoginDetailsResponse {

    @SerializedName("Status")
    private Boolean status;

    @SerializedName("Message")
    private String msg;

    @SerializedName("MemberData")
    private UserDetail data;


    public LoginDetailsResponse(Boolean status, String msg, UserDetail appdata)
    {
        this.status = status;
        this.msg = msg;
        this.data = appdata;

    }


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public UserDetail getData() {
        return data;
    }

    public void setData(UserDetail data) {
        this.data = data;
    }
}
