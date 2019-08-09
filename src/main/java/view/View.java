package view;

public class View {
    private String url;
    private String dispatchAction = "forward";

    public View(String url) {
        this.url = url;
    }

    public View(String url, String dispatchAction) {
        this.url = url;
        this.dispatchAction = dispatchAction;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDispatchAction() {
        return dispatchAction;
    }

    public void setDispatchAction(String dispatchAction) {
        this.dispatchAction = dispatchAction;
    }
}
