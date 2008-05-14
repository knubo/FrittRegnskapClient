package no.knubo.accounting.client.misc;

public enum WidgetIds {
    LINE_EDIT_VIEW(1), REGISTER_MEMBERSHIP(2), SETTINGS(3), ADD_PERSON(4), SHOW_MONTH(5), SHOW_MEMBERS(
            6), FIND_PERSON(7), SHOW_CLASS_MEMBERS(8), SHOW_TRAINING_MEMBERS(9), ABOUT(10), REGISTER_HAPPENING(
            11), EDIT_PRICES(12), REPORT_MEMBER_PER_YEAR(13), SHOW_MONTH_DETAILS(14), EDIT_HAPPENING(
            15), EDIT_PROJECTS(16), END_YEAR(17), END_MONTH(18), TRUST_STATUS(19), REPORT_USERS_EMAIL(
            20), EDIT_TRUST_ACTIONS(21), EDIT_TRUST(22), REPORT_ADDRESSES(23), REPORT_SELECTEDLINES(
            24), LOGOUT(25), EDIT_USERS(26), EDIT_ACCOUNTS(27), REPORT_LETTER(28), REPORT_EMAIL(29), REPORT_ACCOUNTTRACK(
            30), EDIT_ACCOUNTTRACK(31), BUDGET(32), SHOW_ALL_MEMBERS(33), EDIT_SEMESTER(34), MANAGE_FILES(
            35);

    private final int helpPageValue;

    WidgetIds(int value) {
        this.helpPageValue = value;
    }

    public int getHelpPageValue() {
        return helpPageValue;
    }

}
