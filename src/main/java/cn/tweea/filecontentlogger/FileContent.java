/*
 * 版权所有 2017 Tweea。
 * 保留所有权利。
 */
package cn.tweea.filecontentlogger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FileContent {
    private String hash;

    private long length;

    private String name;

    private String path;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("hash", hash).append("length", length).append("name", name).append("path", path)
            .toString();
    }
}
