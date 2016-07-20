import org.glassfish.jersey.client.rx.rxjava.RxObservable;

import static org.junit.Assert.assertEquals;

import org.json.JSONArray;
import org.junit.Test;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import rx.Observable;
import rx.observers.TestSubscriber;


/**
 * Created by anderslime on 7/13/16.
 */
public class TestHelloWorld {
    @Test
    public void testTestMethod() {
        TestSubscriber<List<String>> testSubscriber = new TestSubscriber<List<String>>();
        Observable<List<String>> userCountObservable = RxObservable.newClient()
            .target("https://api.github.com/users")
                .request()
                .rx()
                .get(String.class)
                .flatMap(TestHelloWorld::parseResponseToJSONList)
                .map(user -> user.getString("login"))
                .take(5)
                .toList();

        userCountObservable.subscribe(value -> System.out.println(value));
        userCountObservable.toBlocking().subscribe(value -> System.out.println("Hello"));
        userCountObservable.toBlocking().subscribe(testSubscriber);
        List<String> expectedUserLogins = Arrays.asList("mojombo", "defunkt", "pjhyett", "wycats", "ezmobius");
        testSubscriber.assertReceivedOnNext(Arrays.asList(expectedUserLogins));
    }

    public static Observable<JSONObject> parseResponseToJSONList(String responseString) {
        JSONArray userJsonList = new JSONArray(responseString);
        List<JSONObject> userList = new ArrayList<JSONObject>();
        for (int i = 0; i < userJsonList.length(); i++) {
            userList.add(userJsonList.getJSONObject(i));
        }
        return Observable.from(userList);
    }
}
