package net.researchgate.azkaban;

import azkaban.user.Role;
import azkaban.user.User;
import azkaban.user.UserManagerException;
import azkaban.utils.Props;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class LdapUserManagerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private LdapUserManager userManager;

    @Before
    public void setUp() throws Exception {
        Props props = getProps();
        userManager = new LdapUserManager(props);
    }

    private Props getProps() {
        Props props = new Props();
        props.put(LdapUserManager.LDAP_HOST, "ldap.forumsys.com");
        props.put(LdapUserManager.LDAP_PORT, "389");
        props.put(LdapUserManager.LDAP_USE_SSL, "false");
        props.put(LdapUserManager.LDAP_USER_BASE, "dc=example,dc=com");
        props.put(LdapUserManager.LDAP_USERID_PROPERTY, "uid");
        props.put(LdapUserManager.LDAP_EMAIL_PROPERTY, "mail");
        props.put(LdapUserManager.LDAP_BIND_ACCOUNT, "cn=read-only-admin,dc=example,dc=com");
        props.put(LdapUserManager.LDAP_BIND_PASSWORD, "password");
        props.put(LdapUserManager.LDAP_ALLOWED_GROUPS, "");
        props.put(LdapUserManager.LDAP_GROUP_SEARCH_BASE, "dc=example,dc=com");
        return props;
    }

    @Test
    public void testGetUser() throws Exception {
        User user = userManager.getUser("gauss", "password");

        assertEquals("gauss", user.getUserId());
    }

    @Test
    public void testGetUserWithInvalidPasswordThrowsUserManagerException() throws Exception {
        thrown.expect(UserManagerException.class);
        userManager.getUser("gauss", "invalid");
    }

    @Test
    public void testGetUserWithInvalidUsernameThrowsUserManagerException() throws Exception {
        thrown.expect(UserManagerException.class);
        userManager.getUser("invalid", "password");
    }

    @Test
    public void testGetUserWithEmptyPasswordThrowsUserManagerException() throws Exception {
        thrown.expect(UserManagerException.class);
        userManager.getUser("gauss", "");
    }

    @Test
    public void testGetUserWithEmptyUsernameThrowsUserManagerException() throws Exception {
        thrown.expect(UserManagerException.class);
        userManager.getUser("", "invalid");
    }

    @Test
    public void testValidateUser() throws Exception {
        assertTrue(userManager.validateUser("gauss"));
        assertFalse(userManager.validateUser("invalid"));
    }

    @Test
    public void testGetRole() throws Exception {
        Role role = userManager.getRole("admin");

        assertTrue(role.getPermission().isPermissionNameSet("ADMIN"));
    }
}