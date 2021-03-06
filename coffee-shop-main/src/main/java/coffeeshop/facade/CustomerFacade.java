package coffeeshop.facade;

import coffeeshop.entity.Customer;
import coffeeshop.entity.UserInfo;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class CustomerFacade extends AbstractFacade<Customer> {

    @PersistenceContext(unitName = "bupt-spirit.projects.coffeeshop")
    private EntityManager em;
    @EJB
    private UserInfoFacade userInfoFacade;

    public CustomerFacade() {
        super(Customer.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Customer findByUsername(String username) {
        UserInfo userInfo = userInfoFacade.findByUsername(username);
        if (userInfo == null) {
            return null;
        } else {
            return userInfo.getCustomer();
        }
    }

}
