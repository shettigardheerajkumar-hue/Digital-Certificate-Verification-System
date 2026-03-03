package com.digitalcert.app;

import com.digitalcert.dao.CertificateDao;
import com.digitalcert.dao.UserDao;
import com.digitalcert.dao.jdbc.CertificateDaoJdbcImpl;
import com.digitalcert.dao.jdbc.UserDaoJdbcImpl;
import com.digitalcert.service.CertificateService;
import com.digitalcert.service.UserService;
import com.digitalcert.ui.AuthMenu;
import com.digitalcert.ui.ConsoleMenu;
import com.digitalcert.util.DBConnectionUtil;

public class Main {

    public static void main(String[] args) {
        DBConnectionUtil dbConnectionUtil = new DBConnectionUtil();
        CertificateDao certificateDao = new CertificateDaoJdbcImpl(dbConnectionUtil);
        CertificateService certificateService = new CertificateService(certificateDao);

        UserDao userDao = new UserDaoJdbcImpl(dbConnectionUtil);
        UserService userService = new UserService(userDao);

        AuthMenu authMenu = new AuthMenu(userService);
        boolean authenticated = authMenu.start();
        if (!authenticated) {
            return;
        }

        ConsoleMenu consoleMenu = new ConsoleMenu(certificateService);
        consoleMenu.start();
    }
}

