package com.nhj.librarymanage.domain.dto;

import com.nhj.librarymanage.model.table.SearchField;
import com.nhj.librarymanage.security.member.Role;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.ui.Model;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberManageRequest {

    @FieldNameConstants
    public record SearchCondition(
            String name,
            String email,
            String phoneNumber
    ) {
        public void applySearchFields(Model model) {
            List<SearchField> searchFields = List.of(
                    SearchField.of(Fields.name, "이름"),
                    SearchField.of(Fields.email, "이메일"),
                    SearchField.of(Fields.phoneNumber, "연락처")
            );

            model.addAttribute("searchFields", searchFields);
        }
    }

    // 생성 요청
    public record Create(
            String loginId,
            String password,
            String name,
            String email,
            String phoneNumber,
            Role role,
            String signupToken
    ) {
    }

    // 수정 요청

    public record Update(
            Long id,
            String name
    ) {
    }

}
