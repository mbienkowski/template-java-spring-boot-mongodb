package com.mbienkowski.template.user;

import com.bol.secure.Encrypted;
import com.mbienkowski.template.db.BaseDocument;
import com.mbienkowski.template.user.device.Device;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@SuperBuilder
@Data
@Document("users")
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@NoArgsConstructor
public class User extends BaseDocument {

    @Id
    @NotNull
    protected UUID id;

    @Default
    private boolean active = true;

    @Email
    @Encrypted
    private String login;

    private String passwordHash;

    @Default
    private List<UserRole> roles = new ArrayList<>();

    @Default
    private List<Device> devices = new ArrayList<>();
}
