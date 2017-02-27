
import com.kles.model.LDAP;
import com.kles.protocol.LdapManager;
import javax.naming.NamingException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jchau
 */
public class ListUsers {

    public static void main(String[] args) throws NamingException, Exception {

        LdapManager ldap = new LdapManager(LDAP.getFromPrefs());
        if (ldap.connect()) {
            ldap.listUsers();
        }
    }
}
