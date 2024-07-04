package it.water.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import it.water.core.api.model.User;
import it.water.core.api.permission.ProtectedEntity;
import it.water.core.api.service.rest.WaterJsonView;
import it.water.core.model.exceptions.ValidationException;
import it.water.core.model.validation.ValidationError;
import it.water.core.permission.action.CrudActions;
import it.water.core.permission.annotations.AccessControl;
import it.water.core.permission.annotations.DefaultRoleAccess;
import it.water.core.validation.annotations.NoMalitiusCode;
import it.water.core.validation.annotations.NotNullOnPersist;
import it.water.core.validation.annotations.ValidPassword;
import it.water.repository.jpa.model.AbstractJpaEntity;
import it.water.user.actions.UserActions;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Collections;

/**
 * @Generated by Water Generator
 * Simple User management class.
 * This modules exposes:
 * - User registration
 * - User pwd reset
 * - User Deletion request
 * <p>
 * This module does not provide login feature since it's a different responsability which can be addressed by authentication modules
 */
//JPA
@Entity
@Embeddable
@Access(AccessType.FIELD)
@Table(name = "w_user", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"}), @UniqueConstraint(columnNames = {"email"})})
//Lombok
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode(of = {"id", "username", "email"})
@AccessControl(availableActions = {CrudActions.SAVE, CrudActions.REMOVE, CrudActions.FIND, CrudActions.FIND_ALL, CrudActions.UPDATE, UserActions.IMPERSONATE, UserActions.ACTIVATE, UserActions.DEACTIVATE},
        rolesPermissions = {
                @DefaultRoleAccess(roleName = WaterUser.DEFAULT_MANAGER_ROLE, actions = {CrudActions.SAVE, CrudActions.REMOVE, CrudActions.FIND, CrudActions.FIND_ALL, CrudActions.UPDATE, UserActions.IMPERSONATE, UserActions.ACTIVATE, UserActions.DEACTIVATE}),
                @DefaultRoleAccess(roleName = WaterUser.DEFAULT_VIEWER_ROLE, actions = {CrudActions.FIND, CrudActions.FIND_ALL}),
                @DefaultRoleAccess(roleName = WaterUser.DEFAULT_EDITOR_ROLE, actions = {CrudActions.SAVE, CrudActions.FIND, CrudActions.FIND_ALL, CrudActions.UPDATE}),
        })
public class WaterUser extends AbstractJpaEntity implements ProtectedEntity, User {
    public static final String DEFAULT_MANAGER_ROLE = "userManager";
    public static final String DEFAULT_VIEWER_ROLE = "userViewer";
    public static final String DEFAULT_EDITOR_ROLE = "userEditor";

    /**
     * String name for HUser
     */
    @JsonView(WaterJsonView.Public.class)
    @NoMalitiusCode
    @NotNullOnPersist
    @NotEmpty
    @NonNull
    @Size(max = 500)
    private String name;
    /**
     * String lastname for HUser
     */
    @JsonView(WaterJsonView.Public.class)
    @NoMalitiusCode
    @NotNullOnPersist
    @NonNull
    @NotEmpty
    @Size(max = 500)
    private String lastname;
    /**
     * String username for HUser
     */
    @JsonView(WaterJsonView.Public.class)
    @NoMalitiusCode
    @NotNullOnPersist
    @NotEmpty
    @NonNull
    @Size(max = 400)
    @Pattern(regexp = "^[A-Za-z0-9\\-\\.\\_]+$", message = "Allowed characters are letters (lower and upper cases) and numbers")
    private String username;
    /**
     * String password for HUser
     */
    @NotNullOnPersist
    @NonNull
    @NotNull
    @NoMalitiusCode
    @ValidPassword
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Setter
    @NonNull
    @JsonIgnore
    private String salt;
    /**
     * Boolean admin for HUser
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    @NonNull
    @Setter
    @JsonView(WaterJsonView.Public.class)
    private boolean admin;

    /**
     * String email for HUser
     */
    @JsonView(WaterJsonView.Public.class)
    @Email
    @NonNull
    @NotEmpty
    @NoMalitiusCode
    @NotNullOnPersist
    private String email;

    /**
     * Boolean which indicate that the user is active or not
     */
    @JsonView(WaterJsonView.Internal.class)
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Setter
    private boolean active;

    /**
     * Boolean which indicate that the user has been logically deleted
     */
    @JsonView(WaterJsonView.Internal.class)
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Setter
    private boolean deleted;

    /**
     * the image path of the user account
     */
    @NoMalitiusCode
    @Setter
    @JsonView(WaterJsonView.Public.class)
    private String imagePath;

    /* ### Registration Fields */
    /**
     * Password confirm
     */
    @NoMalitiusCode
    @Transient
    @JsonView(WaterJsonView.Internal.class)
    @Setter
    private String passwordConfirm;

    @NoMalitiusCode
    @JsonIgnore
    @Setter
    private String passwordResetCode;

    /**
     * Code for activation used to confirm registration by email
     */
    @NoMalitiusCode
    @Setter
    @JsonIgnore
    private String activateCode;

    /**
     * Code used for user deletion
     */
    @JsonIgnore
    @NoMalitiusCode
    @Setter
    public String deletionCode;

    //Validation will be done at persist time
    public void updateAccountInfo(String name, String lastname, String email, String username) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.username = username;
    }

    public void updatePassword(byte[] salt, String password, String passwordConfirm) {
        if (password != null && passwordConfirm != null && !password.isBlank() && !passwordConfirm.isBlank() && password.equals(passwordConfirm)) {
            this.salt = new String(salt);
            this.password = password;
            this.passwordConfirm = passwordConfirm;
            return;
        }
        throw new ValidationException(Collections.singletonList(new ValidationError("Password do not match or invalid", "password", "-")));
    }

}