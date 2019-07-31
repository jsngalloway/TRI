package jsngalloway.trulyrandomimgur.ui;

import android.util.Log;
import androidx.lifecycle.ViewModel;

public class FragViewModel extends ViewModel {

    // TODO: Implement the ViewModel
    public FragViewModel() {
    }

    void doAction(String str) {
        Log.e("ViewModel","Action:" + str);
    }
}
