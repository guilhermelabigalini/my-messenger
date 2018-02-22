package my.messenger.androidclient.ui.async;

/**
 * Created by guilherme on 1/27/2018.
 */

public interface AsyncHelperCompleted<TResult> {
    void completed(AsyncHelperFunctionResult<TResult> result);
}
