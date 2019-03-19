package global.redefine.watchdog.utils;

import com.alibaba.fastjson.JSON;
import com.redefine.redis.utils.RedefineClusterUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Map;

/**
 * Created by luqiang on 2018/5/14.
 */
public class GitUtils {

    private static Logger LOG = LoggerFactory.getLogger(GitUtils.class);

    public final static ThreadLocal<Map<String, String>> CONFIG_DATA = new ThreadLocal<>();

    /**
     * 克隆本地仓库
     *
     * @param uri
     * @param localRepoPath
     * @return
     */
    public static Git createLocalGit(String uri, String localRepoPath, String username, String password, String branch) {

        try {


            CloneCommand git = Git.cloneRepository();

            if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
                UsernamePasswordCredentialsProvider passwordCredentialsProvider = new
                        UsernamePasswordCredentialsProvider(username, password);
                git.setCredentialsProvider(passwordCredentialsProvider);
            }
            return git.setURI(uri).setBranch(branch).setDirectory(new File(localRepoPath)).call();

        } catch (Exception e) {
            LOG.error("create local git Repository error :{}", e);

        }
        return null;
    }

    /**
     * pull branch to local
     *
     * @param localPath
     * @return
     */
    public static boolean pullBranchToLocal(String localPath, String username, String password, String branch) {

        boolean resultFlag = false;

        try {
            //git仓库地址
            UsernamePasswordCredentialsProvider passwordCredentialsProvider = new
                    UsernamePasswordCredentialsProvider(username, password);

            Git git = new Git(new FileRepository(localPath + "/.git"));
            git.pull().setRemoteBranchName(branch).setCredentialsProvider(passwordCredentialsProvider).call();
            resultFlag = true;
        } catch (Exception e) {
            LOG.error("pullBranchToLocal  error :{}", e);
        }
        return resultFlag;
    }


    public static void deleteAndCreat(String uri, String localRepoPath, String username, String password, String branch) {

        File file = new File(localRepoPath);
        if (file.exists()) {
            deleteFolder(file);
        }

        createLocalGit(uri, localRepoPath, username, password, branch);
    }

    /**
     * 切换分支
     *
     * @param localPath
     * @param branchName
     */
    public static boolean checkoutBranch(String localPath, String branchName, String username, String password) {
        boolean result = true;
        try {
            Git git = Git.open(new File(localPath + "/.git"));

            git.checkout().setCreateBranch(true).setName(branchName).call();

            UsernamePasswordCredentialsProvider passwordCredentialsProvider = new
                    UsernamePasswordCredentialsProvider(username, password);

            git.pull().setRemoteBranchName(branchName).setCredentialsProvider(passwordCredentialsProvider).call();
            return result;
        } catch (RefAlreadyExistsException re) {
            LOG.warn("RefAlreadyExistsException branchName:{}", branchName);
        } catch (Exception e) {
            LOG.warn("checkoutBranch error:{}", e);
            result = false;
        }
        return result;
    }


    /**
     * 更新配置，如没有修改将不会更新
     *
     * @param localRepoPath /tmp/config/config/.git
     * @param username
     * @param password
     */
    public static boolean commitAndPush(String localRepoPath, String username, String password, String msg, String branch) throws Exception {

        boolean reslut = false;
        pullBranchToLocal(localRepoPath, username, password, branch);

        Git git = Git.open(new File(localRepoPath + "/.git"));


        //将文件提交到git仓库中，并返回本次提交的版本号
        AddCommand addCmd = git.add();

        addCmd.addFilepattern(".").call();

        CommitCommand commitCmd = git.commit();

        commitCmd.setMessage(msg).setAllowEmpty(false).setAll(true).call();

        UsernamePasswordCredentialsProvider passwordCredentialsProvider = new
                UsernamePasswordCredentialsProvider(username, password);

        PushCommand push = git.push();
        push.setCredentialsProvider(passwordCredentialsProvider).call();
        reslut = true;

        return reslut;
    }


    /**
     * delete files
     *
     * @param file
     */
    public static void deleteFolder(File file) {


        if (file.isFile() || file.list().length == 0) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFolder(files[i]);
                files[i].delete();
            }
        }
    }

    public static String getToken(HttpServletRequest request) {

        String accessToken = request.getParameter("token");

        return accessToken;
    }

    public static String getUserData(HttpServletRequest request) {
        String token = GitUtils.getToken(request);
        Object userData = RedefineClusterUtils.getString("watchdog:login:token:" + token);
        Map<String, String> result = JSON.parseObject(String.valueOf(userData), Map.class);

        if (result != null) {
            String username = result.get("username");
            token = StringUtils.isEmpty(username) ? token : username;
        }
        return token;
    }

    /**
     * 初始化git 仓库
     *
     * @param localPath 本地存储目录
     * @param branch    分支
     * @param username  用户名
     * @param password  密码
     * @param uri       git 地址
     */
    public static void init(String localPath, String branch, String username, String password, String uri) {

        File file = new File(localPath);
        if (!file.exists()) {
            file.mkdirs();
            LOG.info("创建GIT文件目录，【{}】", localPath);
        }
        boolean check = GitUtils.checkoutBranch(localPath, branch, username, password);
//        boolean result = GitUtils.pullBranchToLocal(localPath, username, password, branch);
        if (!check) {
            GitUtils.deleteAndCreat(uri, localPath, username, password, branch);
        }

    }


    public static Map<String, String> initConfigData(HttpServletRequest request) {

        Map<String, String> config = initConfig(request);

        String localPath = config.get("redefine.git.localPath");
        String branch = config.get("redefine.git.branch");
        String username = config.get("redefine.git.username");
        String password = config.get("redefine.git.password");
        String uri = config.get("redefine.git.uri");
        //init git
        init(localPath, branch, username, password, uri);

        return config;
    }

    public static Map<String, String> initConfig(HttpServletRequest request) {

        String project = request.getHeader("project");
        String profile = request.getHeader("profile");
        if(StringUtils.isEmpty(profile) || StringUtils.isEmpty(project)){
            LOG.error("git initConfig project & profile can not null, check request header params");
        }
        //获取配置数据
        Object configData = RedefineClusterUtils.hashGet(Constant.WATCHDOG_PROFILE + project, profile);
        Map<String, String> config = JSON.parseObject((String) configData, Map.class);
        config.put("profile", profile);
        config.put("project", project);
        CONFIG_DATA.set(config);

        return config;
    }

    public static void remove() {
        CONFIG_DATA.remove();

    }
}
