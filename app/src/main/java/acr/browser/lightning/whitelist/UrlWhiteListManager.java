package acr.browser.lightning.whitelist;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * 白名单
 * Created by Jin Liang on 2017/6/13.
 */

@Singleton
public class UrlWhiteListManager {

    private List<String> urlWhiteList;

    @Inject
    UrlWhiteListManager() {
    }

    public void setUrlurlWhiteList(List<String> urlList){
        this.urlWhiteList = urlList;
    }

    public boolean validateUrl(String targetUrl){
        if (targetUrl.contains("chinaedu.com")) {
            return true;
        } else {
            if (urlWhiteList != null && !urlWhiteList.isEmpty()) {
                for (int i = 0; i < urlWhiteList.size(); i++) {
                    if (targetUrl.contains(urlWhiteList.get(i))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
