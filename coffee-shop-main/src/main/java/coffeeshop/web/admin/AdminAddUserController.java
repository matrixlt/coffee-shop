package coffeeshop.web.admin;

import coffeeshop.ejb.InitManager;
import coffeeshop.ejb.StoreManager;
import coffeeshop.ejb.StoreManagerException;
import coffeeshop.ejb.UserManager;
import coffeeshop.entity.Store;
import coffeeshop.web.util.MessageBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Collection;
import java.util.logging.Logger;

@Named
@SessionScoped
public class AdminAddUserController implements Serializable {

    private static final Logger LOG = Logger.getLogger(AdminAddUserController.class.getName());

    private static final long serialVersionUID = 1L;

    @EJB
    private UserManager userManager;

    @Inject
    private MessageBundle bundle;

    @EJB
    private InitManager initManager;

    @EJB
    private StoreManager storeManager;

    private String username;
    private String password;
    private String role;
    private String nickname;
    private Collection<String> roles;
    private Store store;

    @PostConstruct
    void init() {
        this.roles = userManager.getRoles();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Collection<String> getRoles() {
        return roles;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public void addUser() throws StoreManagerException {
        if (userManager.isUserExisting(username)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    bundle.getFormatted("Ui.Admin.Message.UserAlreadyExists", username)
            ));
        } else {
            switch (role) {
                case "customer":
                    if (nickname == null || nickname.isEmpty()) {
                        nickname = username;
                    }
                    userManager.addCustomer(username, password, nickname);
                    break;
                case "admin":
                    userManager.addAdmin(username, password);
                    break;
                case "staff":
                    userManager.addStaff(username, password, store);
                    break;
                default:
                    throw new ValidatorException(new FacesMessage(
                            bundle.getString("Ui.Admin.Message.InvalidRole")
                    ));
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    bundle.getFormatted("Ui.Admin.Message.AddUserSuccess", username, role)
            ));
        }
    }
}
