package my.messenger.androidclient.ui.async;

public interface AsyncHelperFunction<TInput,TResult> {

    TResult execute(TInput i) throws Exception ;

}
