import fs from 'fs';

/**
 * 简化版本号自动升级脚本
 * 功能：打包时自动升级版本号
 * 规则：当patch版本号达到9时，进位到minor；当minor达到9时，进位到major
 */
class VersionBumper {
    constructor() {
        this.envFiles = {
            development: '.env.development',
            production: '.env.production'
        };
        this.versionRecordFile = 'env-version-record.js';
    }

    /**
     * 解析版本号字符串
     */
    parseVersion(versionStr) {
        const cleanVersion = versionStr.trim().replace(/['"]/g, '');
        const parts = cleanVersion.split('.');
        
        if (parts.length !== 3) {
            throw new Error(`Invalid version format: ${versionStr}`);
        }

        return {
            major: parseInt(parts[0], 10),
            minor: parseInt(parts[1], 10),
            patch: parseInt(parts[2], 10)
        };
    }

    /**
     * 格式化版本号
     */
    formatVersion(version) {
        return `${version.major}.${version.minor}.${version.patch}`;
    }

    /**
     * 升级版本号
     */
    bumpVersion(currentVersion) {
        const newVersion = { ...currentVersion };

        // 先升级patch版本号
        newVersion.patch += 1;
        
        // 如果patch达到10，进位到minor
        if (newVersion.patch >= 10) {
            newVersion.minor += 1;
            newVersion.patch = 0;
            
            // 如果minor达到10，进位到major
            if (newVersion.minor >= 10) {
                newVersion.major += 1;
                newVersion.minor = 0;
            }
        }

        return newVersion;
    }

    /**
     * 读取环境文件并提取版本号
     */
    getCurrentVersion(envType) {
        const envFile = this.envFiles[envType];
        
        if (!fs.existsSync(envFile)) {
            throw new Error(`Environment file not found: ${envFile}`);
        }

        const content = fs.readFileSync(envFile, 'utf8');
        const versionRegex = /VITE_BUILD_VERSION\s*=\s*(.+)/;
        const match = content.match(versionRegex);
        
        if (!match) {
            throw new Error('VITE_BUILD_VERSION not found');
        }

        return this.parseVersion(match[1].trim());
    }

    /**
     * 更新环境文件中的版本号
     */
    updateVersionInFile(filePath, newVersion) {
        const content = fs.readFileSync(filePath, 'utf8');
        const versionRegex = /(VITE_BUILD_VERSION\s*=\s*)(.+)/;
        const updatedContent = content.replace(versionRegex, `$1${newVersion}`);
        fs.writeFileSync(filePath, updatedContent, 'utf8');
    }

    /**
     * 获取当前时间字符串
     */
    getCurrentTimeString() {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        
        return `${year}-${month}-${day} ${hours}:${minutes}`;
    }

    /**
     * 读取版本记录文件
     */
    readVersionRecord() {
        if (!fs.existsSync(this.versionRecordFile)) {
            return {};
        }

        try {
            const content = fs.readFileSync(this.versionRecordFile, 'utf8');
            // 提取versionRecord对象内容
            const match = content.match(/let versionRecord = \{([\s\S]*?)\}/);
            if (!match) {
                return {};
            }

            // 解析版本记录，包含时间信息和额外注释
            const recordContent = match[1];
            const lines = recordContent.split('\n');
            
            const record = {};
            let currentTime = '';
            let pendingComments = [];
            
            lines.forEach(line => {
                const trimmedLine = line.trim();
                
                // 跳过空行
                if (!trimmedLine) {
                    return;
                }
                
                // 匹配时间注释
                const timeMatch = trimmedLine.match(/\/\/ 打包时间 (.+)/);
                if (timeMatch) {
                    currentTime = timeMatch[1];
                    pendingComments = []; // 重置待处理注释
                    return;
                }
                
                // 匹配版本记录
                const versionMatch = trimmedLine.match(/"(v\d+)": "([^"]+)"/);
                if (versionMatch && currentTime) {
                    const [, key, version] = versionMatch;
                    record[key] = {
                        version: version,
                        time: currentTime,
                        comments: [...pendingComments] // 使用之前收集的注释
                    };
                    pendingComments = []; // 清空已使用的注释
                    return;
                }
                
                // 匹配其他注释（用户添加的更新信息）
                const commentMatch = trimmedLine.match(/\/\/ (.+)/);
                if (commentMatch) {
                    const commentText = commentMatch[1];
                    if (!commentText.startsWith('打包时间')) {
                        pendingComments.push(commentText);
                    }
                }
            });

            return record;
        } catch (error) {
            console.warn('Warning: Could not parse version record file, creating new one');
            return {};
        }
    }

    /**
     * 更新版本记录文件
     */
    updateVersionRecord(newVersion) {
        const currentRecord = this.readVersionRecord();
        const currentTime = this.getCurrentTimeString();
        
        // 获取当前最大的版本号
        const versionKeys = Object.keys(currentRecord);
        let maxVersionNum = 0;
        
        versionKeys.forEach(key => {
            const num = parseInt(key.replace('v', ''), 10);
            if (num > maxVersionNum) {
                maxVersionNum = num;
            }
        });

        // 创建新的版本记录
        const newVersionKey = `v${maxVersionNum + 1}`;
        const newRecord = {
            [newVersionKey]: {
                version: newVersion,
                time: currentTime,
                comments: [] // 新版本暂无注释，用户可以手动添加
            },
            ...currentRecord
        };

        // 生成文件内容
        let content = 'let versionRecord = {\n\n';
        
        Object.entries(newRecord).forEach(([key, data]) => {
            content += `   // 打包时间 ${data.time}\n`;
            
            // 添加用户的额外注释
            if (data.comments && data.comments.length > 0) {
                data.comments.forEach(comment => {
                    content += `   // ${comment}\n`;
                });
            }
            
            content += `  "${key}": "${data.version}",\n`;
        });
        
        // 移除最后一个逗号
        content = content.replace(/,\n$/, '\n');
        content += '}';

        // 写入文件
        fs.writeFileSync(this.versionRecordFile, content, 'utf8');
        console.log(`📝 Updated ${this.versionRecordFile}: Added ${newVersionKey} = ${newVersion}`);
    }

    /**
     * 执行版本号升级
     */
    bump(envType) {
        try {
            const envFile = this.envFiles[envType];
            
            if (!envFile) {
                throw new Error(`Invalid environment type: ${envType}`);
            }

            const currentVersion = this.getCurrentVersion(envType);
            const currentVersionStr = this.formatVersion(currentVersion);
            
            const newVersion = this.bumpVersion(currentVersion);
            const newVersionStr = this.formatVersion(newVersion);
            
            console.log(`[${envType.toUpperCase()}] Version: ${currentVersionStr} → ${newVersionStr}`);

            // 更新环境文件
            this.updateVersionInFile(envFile, newVersionStr);
            console.log(`✅ Updated ${envFile}: VITE_BUILD_VERSION = ${newVersionStr}`);

            // 如果是生产环境，更新版本记录文件
            if (envType === 'production') {
                this.updateVersionRecord(newVersionStr);
            }

            return newVersionStr;

        } catch (error) {
            console.error('❌ Error:', error.message);
            process.exit(1);
        }
    }
}

// 执行脚本
const bumper = new VersionBumper();
const args = process.argv.slice(2);
const envType = args[0];

if (envType === 'dev') {
    bumper.bump('development');
} else if (envType === 'prod') {
    bumper.bump('production');
} else {
    console.error('❌ Please specify environment: dev or prod');
    process.exit(1);
} 