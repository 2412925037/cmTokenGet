package com.surprise.shuabasejoymenghull;

import android.test.AndroidTestCase;
import android.util.Log;

import com.use.nice.NiceCts;
import com.use.nice.manager.GlobalContext;
import com.use.nice.update.UDCtrl;

/**
 * Created by zhengnan on 2016/1/22.
 */
public class FunctionTest extends AndroidTestCase{

    //检测请求后台时，加密解密是否正确。
    public void testNetlink() {
        GlobalContext.init(mContext);
        String ret = UDCtrl.getIns().pullData(NiceCts.UPDATE_URL, null);
        Log.e("J_Nice", ret);
    }
}
