/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2015] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.im.artifacts;

import com.codenvy.im.BaseTest;
import com.codenvy.im.backup.BackupConfig;
import com.codenvy.im.command.Command;
import com.codenvy.im.config.Config;
import com.codenvy.im.config.ConfigUtil;
import com.codenvy.im.exceptions.UnknownInstallationTypeException;
import com.codenvy.im.install.InstallOptions;
import com.codenvy.im.install.InstallType;
import com.codenvy.im.utils.HttpTransport;
import com.codenvy.im.utils.OSUtils;
import com.codenvy.im.utils.Version;
import com.google.common.collect.ImmutableMap;

import org.mockito.Mock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Anatoliy Bazko
 * @author Dmytro Nochevnov
 */
public class TestCDECArtifact extends BaseTest {
    private static final String initialOsVersion = OSUtils.VERSION;
    private CDECArtifact  spyCdecArtifact;
    @Mock
    private HttpTransport transport;
    @Mock
    private ConfigUtil    configUtil;
    @Mock
    private Config        config;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        spyCdecArtifact = spy(new CDECArtifact(transport, configUtil));
    }

    @AfterMethod
    public void tearDown() throws Exception {
        OSUtils.VERSION = initialOsVersion;
    }

    @Test
    public void testGetInstallSingleServerInfo() throws Exception {
        InstallOptions options = new InstallOptions();
        options.setInstallType(InstallType.SINGLE_SERVER);
        options.setStep(1);

        List<String> info = spyCdecArtifact.getInstallInfo(options);
        assertNotNull(info);
        assertTrue(info.size() > 1);
    }

    @Test
    public void testGetInstallMultiServerInfo() throws Exception {
        InstallOptions options = new InstallOptions();
        options.setInstallType(InstallType.MULTI_SERVER);
        options.setStep(1);

        List<String> info = spyCdecArtifact.getInstallInfo(options);
        assertNotNull(info);
        assertTrue(info.size() > 1);
    }

    @Test
    public void testGetInstallSingleServerCommandOsVersion6() throws Exception {
        OSUtils.VERSION = "6";

        InstallOptions options = new InstallOptions();
        options.setInstallType(InstallType.SINGLE_SERVER);
        options.setConfigProperties(ImmutableMap.of("some property", "some value"));

        int steps = spyCdecArtifact.getInstallInfo(options).size();
        for (int i = 0; i < steps; i++) {
            options.setStep(i);
            Command command = spyCdecArtifact.getInstallCommand(null, Paths.get("some path"), options);
            assertNotNull(command);
        }
    }

    @Test
    public void testGetInstallSingleServerCommandOsVersion7() throws Exception {
        OSUtils.VERSION = "7";

        InstallOptions options = new InstallOptions();
        options.setInstallType(InstallType.SINGLE_SERVER);
        options.setConfigProperties(ImmutableMap.of("some property", "some value"));

        int steps = spyCdecArtifact.getInstallInfo(options).size();
        for (int i = 0; i < steps; i++) {
            options.setStep(i);
            Command command = spyCdecArtifact.getInstallCommand(null, Paths.get("some path"), options);
            assertNotNull(command);
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetInstallSingleServerCommandError() throws Exception {
        InstallOptions options = new InstallOptions();
        options.setConfigProperties(Collections.<String, String>emptyMap());
        options.setInstallType(InstallType.SINGLE_SERVER);
        options.setStep(Integer.MAX_VALUE);

        spyCdecArtifact.getInstallCommand(null, Paths.get("some path"), options);
    }

    @Test
    public void testGetInstallMultiServerCommandsForMultiServer() throws Exception {
        OSUtils.VERSION = "7";

        InstallOptions options = new InstallOptions();
        options.setInstallType(InstallType.MULTI_SERVER);
        options.setConfigProperties(ImmutableMap.of("site_host_name", "site.example.com"));

        int steps = spyCdecArtifact.getInstallInfo(options).size();
        for (int i = 0; i < steps; i++) {
            options.setStep(i);
            Command command = spyCdecArtifact.getInstallCommand(null, Paths.get("some path"), options);
            assertNotNull(command);
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Site node config not found.")
    public void testGetInstallMultiServerCommandsForMultiServerError() throws Exception {
        OSUtils.VERSION = "7";

        InstallOptions options = new InstallOptions();
        options.setInstallType(InstallType.MULTI_SERVER);
        options.setConfigProperties(ImmutableMap.of("some property", "some value"));

        int steps = spyCdecArtifact.getInstallInfo(options).size();
        for (int i = 0; i < steps; i++) {
            options.setStep(i);
            Command command = spyCdecArtifact.getInstallCommand(null, Paths.get("some path"), options);
            assertNotNull(command);
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Step number .* is out of install range")
    public void testGetInstallMultiServerCommandUnexistedStepError() throws Exception {
        OSUtils.VERSION = "7";

        InstallOptions options = new InstallOptions();
        options.setConfigProperties(Collections.<String, String>emptyMap());
        options.setInstallType(InstallType.MULTI_SERVER);
        options.setStep(Integer.MAX_VALUE);

        spyCdecArtifact.getInstallCommand(null, Paths.get("some path"), options);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetInstallMultiServerCommandsWrongOS() throws Exception {
        OSUtils.VERSION = "6";

        InstallOptions options = new InstallOptions();
        options.setInstallType(InstallType.MULTI_SERVER);
        options.setConfigProperties(ImmutableMap.of("some property", "some value"));
        options.setStep(1);

        spyCdecArtifact.getInstallCommand(null, null, options);
    }

    @Test
    public void getInstalledVersionShouldReturnNullIfPuppetConfigAbsent() throws Exception {
        doThrow(UnknownInstallationTypeException.class).when(configUtil).loadInstalledCodenvyConfig();
        assertNull(spyCdecArtifact.getInstalledVersion());
    }

    @Test
    public void getInstalledVersionShouldReturnNullIfConfigAbsent() throws Exception {
        doReturn(InstallType.SINGLE_SERVER).when(configUtil).detectInstallationType();
        doThrow(IOException.class).when(configUtil).loadInstalledCodenvyConfig();

        assertNull(spyCdecArtifact.getInstalledVersion());
    }

    @Test
    public void getInstalledVersionShouldReturnNullIfRequestThrowException() throws Exception {
        prepareSingleNodeEnv(configUtil, transport);
        doThrow(IOException.class).when(transport).doOption("http://hostname/api/", null);

        assertNull(spyCdecArtifact.getInstalledVersion());
    }

    @Test
    public void getInstalledVersionShouldReturnVersion() throws Exception {
        prepareSingleNodeEnv(configUtil, transport);

        assertEquals(spyCdecArtifact.getInstalledVersion(), Version.valueOf("3.3.0"));
    }

    @Test
    public void getInstalledVersionShouldReturnNullIfRequestEmpty() throws Exception {
        prepareSingleNodeEnv(configUtil, transport);
        doReturn("").when(transport).doOption("http://hostname/api/", null);

        assertNull(spyCdecArtifact.getInstalledVersion());
    }

    @Test
    public void getInstalledVersionShouldReturn310CodenvyVersion() throws Exception {
        prepareSingleNodeEnv(configUtil, transport);
        when(transport.doOption("http://hostname/api/", null)).thenReturn("{\"implementationVersion\":\"0.26.0\"}");

        assertEquals(spyCdecArtifact.getInstalledVersion(), Version.valueOf("3.1.0"));
    }

    @Test
    public void testGetUpdateSingleServerCommand() throws Exception {
        prepareSingleNodeEnv(configUtil, transport);

        InstallOptions options = new InstallOptions();
        options.setConfigProperties(ImmutableMap.of("some property", "some value"));
        options.setInstallType(InstallType.SINGLE_SERVER);

        int steps = spyCdecArtifact.getUpdateInfo(options).size();
        for (int i = 0; i < steps; i++) {
            options.setStep(i);
            Command command = spyCdecArtifact.getUpdateCommand(Version.valueOf("4.0.0"), Paths.get("some path"), options);
            assertNotNull(command);
        }
    }

    @Test
    public void testGetUpdateMultiServerCommand() throws Exception {
        prepareMultiNodeEnv(configUtil, transport);

        InstallOptions options = new InstallOptions();
        options.setConfigProperties(ImmutableMap.of("some property", "some value"));
        options.setInstallType(InstallType.MULTI_SERVER);

        int steps = spyCdecArtifact.getUpdateInfo(options).size();
        for (int i = 0; i < steps; i++) {
            options.setStep(i);
            Command command = spyCdecArtifact.getUpdateCommand(Version.valueOf("4.0.0"), Paths.get("some path"), options);
            assertNotNull(command);
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Step number .* is out of update range")
    public void testGetUpdateCommandUnexistedStepError() throws Exception {
        prepareSingleNodeEnv(configUtil, transport);

        OSUtils.VERSION = "7";

        InstallOptions options = new InstallOptions();
        options.setConfigProperties(Collections.<String, String>emptyMap());
        options.setInstallType(InstallType.SINGLE_SERVER);
        options.setStep(Integer.MAX_VALUE);

        spyCdecArtifact.getUpdateCommand(Version.valueOf("1.0.0"), Paths.get("some path"), options);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Only update to the Codenvy of the same installation type is supported")
    public void testGetUpdateInfoFromSingleToMultiServerError() throws Exception {
        prepareSingleNodeEnv(configUtil, transport);

        OSUtils.VERSION = "7";

        InstallOptions options = new InstallOptions();
        options.setConfigProperties(Collections.<String, String>emptyMap());
        options.setInstallType(InstallType.MULTI_SERVER);
        options.setStep(0);

        spyCdecArtifact.getUpdateInfo(options);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Only update to the Codenvy of the same installation type is supported")
    public void testGetUpdateInfoFromMultiToSingleServerError() throws Exception {
        prepareMultiNodeEnv(configUtil, transport);

        OSUtils.VERSION = "7";

        InstallOptions options = new InstallOptions();
        options.setConfigProperties(Collections.<String, String>emptyMap());
        options.setInstallType(InstallType.SINGLE_SERVER);
        options.setStep(0);

        spyCdecArtifact.getUpdateInfo(options);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Only update to the Codenvy of the same installation type is supported")
    public void testGetUpdateCommandFromSingleToMultiServerError() throws Exception {
        prepareSingleNodeEnv(configUtil, transport);

        OSUtils.VERSION = "7";

        InstallOptions options = new InstallOptions();
        options.setConfigProperties(Collections.<String, String>emptyMap());
        options.setInstallType(InstallType.MULTI_SERVER);
        options.setStep(0);

        spyCdecArtifact.getUpdateCommand(Version.valueOf("1.0.0"), Paths.get("some path"), options);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Only update to the Codenvy of the same installation type is supported")
    public void testGetUpdateCommandFromMultiToSingleServerError() throws Exception {
        prepareMultiNodeEnv(configUtil, transport);

        OSUtils.VERSION = "7";

        InstallOptions options = new InstallOptions();
        options.setConfigProperties(Collections.<String, String>emptyMap());
        options.setInstallType(InstallType.SINGLE_SERVER);
        options.setStep(0);

        spyCdecArtifact.getUpdateCommand(Version.valueOf("1.0.0"), Paths.get("some path"), options);
    }

    @Test
    public void testGetBackupSingleServerCommand() throws Exception {
        prepareSingleNodeEnv(configUtil, transport);

        BackupConfig backupConfig = new BackupConfig().setArtifactName(CDECArtifact.NAME);
        backupConfig.setBackupFile(backupConfig.generateBackupFilePath());

        assertNotNull(spyCdecArtifact.getBackupCommand(backupConfig, configUtil));
    }

    @Test
    public void testGetBackupMultiServerCommand() throws Exception {
        prepareMultiNodeEnv(configUtil, transport);

        BackupConfig backupConfig = new BackupConfig().setArtifactName(CDECArtifact.NAME);
        backupConfig.setBackupFile(backupConfig.generateBackupFilePath());

        assertNotNull(spyCdecArtifact.getBackupCommand(backupConfig, configUtil));
    }

    @Test
    public void testGetRestoreSingleServerCommand() throws Exception {
        prepareSingleNodeEnv(configUtil, transport);

        BackupConfig backupConfig = new BackupConfig().setArtifactName(CDECArtifact.NAME)
                                                      .setBackupFile(Paths.get("dummyFile"))
                                                      .setBackupDirectory(Paths.get("dummyDirectory"));

        assertNotNull(spyCdecArtifact.getRestoreCommand(backupConfig, configUtil));
    }

    @Test
    public void testGetRestoreMultiServerCommand() throws Exception {
        prepareMultiNodeEnv(configUtil, transport);

        BackupConfig backupConfig = new BackupConfig().setArtifactName(CDECArtifact.NAME)
                                                      .setBackupFile(Paths.get("dummyFile"))
                                                      .setBackupDirectory(Paths.get("dummyDirectory"));

        assertNotNull(spyCdecArtifact.getRestoreCommand(backupConfig, configUtil));
    }

}