package tianxingzhe.plugin.utils.beans;

import java.io.Serializable;

/**
 * Created by chenguowu on 2019/1/9.
 */
 public class BaseBean implements Serializable {
 protected String code;
 protected String status;
 protected String timestamp;
 protected String message;

 public String getCode() {
  return code;
 }

 public void setCode(String code) {
  this.code = code;
 }

 public String getMessage() {
  return message;
 }

 public void setMessage(String message) {
  this.message = message;
 }

 public String getStatus() {
  return status;
 }

 public void setStatus(String status) {
  this.status = status;
 }

 public String getTimestamp() {
  return timestamp;
 }

 public void setTimestamp(String timestamp) {
  this.timestamp = timestamp;
 }
}
