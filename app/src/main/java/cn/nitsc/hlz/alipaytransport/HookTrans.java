package cn.nitsc.hlz.alipaytransport;

import android.app.Activity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookTrans implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("com.eg.android.AlipayGphone")) {
            final Class<?> clazz = XposedHelpers.findClass("com.alipay.mobile.base.security.CI",lpparam.classLoader);
            final Class<?> HttpHost_class = XposedHelpers.findClass("org.apache.http.HttpHost",lpparam.classLoader);
            final Class<?> HttpRequest_class = XposedHelpers.findClass("org.apache.http.HttpRequest",lpparam.classLoader);
            final Class<?> HttpContext_class = XposedHelpers.findClass("org.apache.http.protocol.HttpContext",lpparam.classLoader);
            final Class<?> reqDate_class = XposedHelpers.findClass("com.alipay.android.app.trans.ReqData",lpparam.classLoader);
            final Class<?> req_conf_class = XposedHelpers.findClass("com.alipay.android.app.trans.config.RequestConfig",lpparam.classLoader);
            // anti anti Xposed
            XposedHelpers.findAndHookMethod(clazz,
                    "a",
                    clazz,
                    Activity.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param){
                            XposedBridge.log("Bypass Application Suicide");
                            return null;
                        }
                    });
            // MRpcClient
            XposedHelpers.findAndHookMethod("com.alipay.mobile.common.transportext.biz.mmtp.mrpc.MRpcClient",
                    lpparam.classLoader,
                    "execute",
                    HttpHost_class,
                    HttpRequest_class,
                    HttpContext_class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            // param.args[0] HttpHost(String hostname, int port, String scheme)
                            // param.args[1] HttpRequest
                            // param.args[2] HttpContext
                            XposedBridge.log("********************* Start MRpcClient *********************\n");
                            Field[] host = param.args[0].getClass().getDeclaredFields();
                            for (int i=0; i< host.length; i++){
                                Field h = host[i];
                                h.setAccessible(true);
                                XposedBridge.log("MRpcClient HOST \tFieldName:"+h.getName()+"\tFieldType:"+h.getType()+"\tFieldValue:"+h.get(param.args[0])+"\n");
                            }
                            Field[] req = param.args[1].getClass().getDeclaredFields();
                            for (int i = 0; i < req.length; i++){
                                Field r = req[i];
                                r.setAccessible(true);
                                XposedBridge.log("MRpcClient REQUEST \tFieldName:"+r.getName()+"\tFieldType:"+r.getType()+"\tFieldValue:"+r.get(param.args[1])+"\n");
                            }
                            Field[] content = param.args[2].getClass().getDeclaredFields();
                            for (int i = 0; i < content.length; i++){
                                Field c = content[i];
                                c.setAccessible(true);
                                XposedBridge.log("MRpcClient Content \tFieldName:"+c.getName()+"\tFieldType:"+c.getType()+"\tFieldValue:"+c.get(param.args[2])+"\n");
                            }
                            XposedBridge.log("********************* Finish *********************\n");
                        }
                    }
            );
            // PbTransportChannel
            XposedHelpers.findAndHookMethod("com.alipay.android.app.pb.api.PbTransportChannel", lpparam.classLoader,
                    "requestByPbv3",
                    reqDate_class,
                    req_conf_class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            XposedBridge.log("********************* Start requestByPbv3*********************\n");
                            Field[] reqdata = param.args[0].getClass().getDeclaredFields();
                            for (int i =0; i< reqdata.length; i++){
                                Field r = reqdata[i];
                                r.setAccessible(true);
                                XposedBridge.log("requestByPbv3 ReqData \tFieldName:"+r.getName()+"\tFieldType:"+r.getType()+"\tFieldValue:"+r.get(param.args[0]));
                            }
                            Field[] reqconf = param.args[1].getClass().getDeclaredFields();
                            for (int i =0; i< reqconf.length; i++){
                                Field r = reqconf[i];
                                r.setAccessible(true);
                                XposedBridge.log("requestByPbv3 ReqConf \tFieldName:"+r.getName()+"\tFieldType:"+r.getType()+"\tFieldValue:"+r.get(param.args[1]));
                            }
                            XposedBridge.log("********************* Finish *********************\n");
                        }
                    }
            );
            // HttpWorker h5 application ?
            XposedHelpers.findAndHookMethod("com.alipay.mobile.common.transport.http.HttpWorker", lpparam.classLoader,
                    "executeHttpClientRequest",
                    HttpHost_class,
                    HttpRequest_class,
                    HttpContext_class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            XposedBridge.log("********************* Start HttpWorker*********************\n");
                            Field[] host = param.args[0].getClass().getDeclaredFields();
                            for (int i=0; i< host.length; i++){
                                Field h = host[i];
                                h.setAccessible(true);
                                XposedBridge.log("HttpWorker HOST \tFieldName:"+h.getName()+"\tFieldType:"+h.getType()+"\tFieldValue:"+h.get(param.args[0])+"\n");
                            }
                            Field[] req = param.args[1].getClass().getDeclaredFields();
                            for (int i = 0; i < req.length; i++){
                                Field r = req[i];
                                r.setAccessible(true);
                                XposedBridge.log("HttpWorker REQUEST \tFieldName:"+r.getName()+"\tFieldType:"+r.getType()+"\tFieldValue:"+r.get(param.args[1])+"\n");
                            }
                            Field[] content = param.args[2].getClass().getDeclaredFields();
                            for (int i = 0; i < content.length; i++){
                                Field c = content[i];
                                c.setAccessible(true);
                                XposedBridge.log("HttpWorker Content\tFieldName:"+c.getName()+"\tFieldType:"+c.getType()+"\tFieldValue:"+c.get(param.args[2])+"\n");
                            }
                            XposedBridge.log("********************* Finish *********************\n");
                        }
                    });
        }
    }
}
