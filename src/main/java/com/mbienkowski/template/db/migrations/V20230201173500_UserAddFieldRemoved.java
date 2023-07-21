package com.mbienkowski.template.db.migrations;

import com.mbienkowski.template.user.User;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@Slf4j
@ChangeUnit(order = "V20230201173500", id = "V20230201173500-add-removed-field-to-user-entity", transactional = false)
@RequiredArgsConstructor
public class V20230201173500_UserAddFieldRemoved {

    private final MongoTemplate mongoTemplate;

    @Execution
    public void migrate() {
        Update update = new Update();
        update.set("removed", false);
        mongoTemplate.updateMulti(new Query(), update, User.class);
    }

    @RollbackExecution
    public void rollback() {
        Update update = new Update();
        update.unset("removed");
        mongoTemplate.updateMulti(new Query(), update, User.class);
    }

}
