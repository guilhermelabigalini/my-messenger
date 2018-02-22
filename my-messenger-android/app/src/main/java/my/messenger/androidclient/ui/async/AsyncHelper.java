package my.messenger.androidclient.ui.async;

import android.os.AsyncTask;


public final class AsyncHelper {

    public static <TInput,TResult> HelperTask<TInput, TResult> runAsync(
            final AsyncHelperFunction<TInput,TResult> action,
            final AsyncHelperCompleted<TResult> onCompleted)  {
        return new HelperTask<>(action, onCompleted, null);
    }

    public static <TInput,TResult> HelperTask<TInput, TResult> runAsync(
            final AsyncHelperFunction<TInput,TResult> action,
            final AsyncHelperCompleted<TResult> onCompleted,
            final AsyncHelperCancelled onCancelled)  {
        return new HelperTask<>(action, onCompleted, onCancelled);
    }

    public static class HelperTask<TInput,TResult> extends AsyncTask<TInput, Void, AsyncHelperFunctionResult<TResult>> {

        private static final String LOGGING = "HelperTask";
        final AsyncHelperFunction<TInput,TResult> action;
        final AsyncHelperCompleted<TResult> onCompleted;
        final AsyncHelperCancelled onCancelled;

        HelperTask(final AsyncHelperFunction<TInput,TResult> action,
                      final AsyncHelperCompleted<TResult> onCompleted,
                      final AsyncHelperCancelled onCancelled) {
            this.action = action;
            this.onCompleted = onCompleted;
            this.onCancelled = onCancelled;
        }

        @Override
        protected void onCancelled() {
            if (onCancelled != null) {
                onCancelled.cancelled();
            }
        }

        @Override
        protected AsyncHelperFunctionResult<TResult> doInBackground(TInput[] tInputs) {

            AsyncHelperFunctionResult<TResult> res = new AsyncHelperFunctionResult<>();
            try {
                res.result = action.execute(tInputs[0]);
            } catch (Exception e) {
                res.err = e;
            }
            return res;
        }

        @Override
        protected void onPostExecute(AsyncHelperFunctionResult<TResult> tResult) {
            if (onCompleted != null) {
                onCompleted.completed(tResult);
            }
        }
    }
}
