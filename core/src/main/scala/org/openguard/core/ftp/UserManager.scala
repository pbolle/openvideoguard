package org.openguard.core.ftp

import javax.sql.DataSource

import org.apache.ftpserver.ftplet.{FtpException, User}
import org.apache.ftpserver.usermanager.PasswordEncryptor
import org.apache.ftpserver.usermanager.impl.{BaseUser, DbUserManager}
import play.api.Play
import play.api.Play.current

class UserManager(dataSource: DataSource, selectAllStmt: String,
                  selectUserStmt: String, insertUserStmt: String,
                  updateUserStmt: String, deleteUserStmt: String,
                  authenticateStmt: String, isAdminStmt: String,
                  passwordEncryptor: PasswordEncryptor, adminName: String) extends DbUserManager(dataSource, selectAllStmt, selectUserStmt, insertUserStmt, updateUserStmt, deleteUserStmt, authenticateStmt, isAdminStmt, passwordEncryptor, adminName) {

  @throws(classOf[FtpException])
  override def getUserByName(name: String): User = {
    var user = super.getUserByName(name)
    var baseuser = new BaseUser;
    baseuser.setAuthorities(user.getAuthorities())
    baseuser.setEnabled(user.getEnabled)
    baseuser.setHomeDirectory(user.getHomeDirectory.replace("${ftphome}", Play.configuration.getString("ovg.ftpDirectory").getOrElse("~/")))
    baseuser.setMaxIdleTime(user.getMaxIdleTime)
    baseuser.setName(user.getName)
    baseuser.setPassword(user.getPassword)

    return baseuser
  }
}
