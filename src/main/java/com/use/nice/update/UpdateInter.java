package com.use.nice.update;

/**
 * Created by zhengnan on 2015/9/15.
 * --统一接口，将动态加载模块化。
 * --
 */
public interface UpdateInter {
    /**
     * @return 当前模块的名称
     */
      String getModuleName();

    /**
     * 执行更新操作
     */
      void update();

    /**
     * @return 是否可获取一个合格的子包的apk文件
     */
      boolean sureApk();

    /**
     * @return 设置的apk的名称
     */
      String getApkName();

    /**
     * @return 获取设置的png名称
     */
      String getPngName();

    /**
     * @return 获取要保存到的path
     */
    public String getSavePath();

    /**
     * 删除释放的apk资源
     */
    void deleteApk();

    /**
     * 删除承载动态包的png文件
     */
    void deletePng();

    /**
     * @return 获取当前底包的版本
     */
      float getVersion();

    /**
     * @return 获取子包写入的版本
     */
      String getSubVersion();
}
