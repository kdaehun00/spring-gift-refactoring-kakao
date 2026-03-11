package gift.member.api;

import gift.member.service.MemberService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Admin controller for managing members.
 *
 * @author brian.kim
 * @since 1.0
 */
@Controller
@RequestMapping("/admin/members")
public class AdminMemberController {
    private static final String VIEW_LIST = "member/list";
    private static final String VIEW_NEW = "member/new";
    private static final String VIEW_EDIT = "member/edit";
    private static final String REDIRECT_LIST = "redirect:/admin/members";

    private static final String ATTR_MEMBERS = "members";
    private static final String ATTR_MEMBER = "member";
    private static final String ATTR_ERROR = "error";
    private static final String ATTR_EMAIL = "email";

    private final MemberService memberService;

    public AdminMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute(ATTR_MEMBERS, memberService.findAll());
        return VIEW_LIST;
    }

    @GetMapping("/new")
    public String newForm() {
        return VIEW_NEW;
    }

    @PostMapping
    public String create(
        @RequestParam String email,
        @RequestParam String password,
        Model model
    ) {
        if (memberService.existsByEmail(email)) {
            populateNewFormError(model, email, "Email is already registered.");
            return VIEW_NEW;
        }

        memberService.register(email, password);
        return REDIRECT_LIST;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute(ATTR_MEMBER, memberService.findById(id));
        return VIEW_EDIT;
    }

    @PostMapping("/{id}/edit")
    public String update(
        @PathVariable Long id,
        @RequestParam String email,
        @RequestParam String password
    ) {
        memberService.update(id, email, password);
        return REDIRECT_LIST;
    }

    @PostMapping("/{id}/charge-point")
    public String chargePoint(
        @PathVariable Long id,
        @RequestParam int amount
    ) {
        memberService.chargePoint(id, amount);
        return REDIRECT_LIST;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        memberService.deleteById(id);
        return REDIRECT_LIST;
    }

    private void populateNewFormError(Model model, String email, String error) {
        model.addAttribute(ATTR_ERROR, error);
        model.addAttribute(ATTR_EMAIL, email);
    }
}
