package acr.browser.lightning.safedomain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * 白名单
 * Created by Jin Liang on 2017/6/13.
 */

@Singleton
public class SafeDomainListManager {

    private List<String> urlWhiteList = Collections.synchronizedList(new ArrayList<String>(5));

    @Inject
    SafeDomainListManager() {
//        urlWhiteList.add("chinaedu.com");
//        urlWhiteList.add("218.25.139.161");// http://218.25.139.161:10046/sunrise/login/login.do 默认主页
    }

    public void addUrl(String url){
        urlWhiteList.add(url);
    }

    public void addAllUrls(Collection<String> urls){
        urlWhiteList.addAll(urls);
    }

    public void removeUrl(String url){
        urlWhiteList.remove(url);
    }

    public void setUrlWhiteList(List<String> urlList){
        this.urlWhiteList = urlList;
    }

    public boolean validateUrl(String targetUrl){
        if (urlWhiteList != null && !urlWhiteList.isEmpty()) {
            for (int i = 0; i < urlWhiteList.size(); i++) {
                if (targetUrl.contains(urlWhiteList.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }
}
