/*
 * The MIT License
 *
 * Copyright 2014 Aleksander.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package pl.grzegorz2047.openguild2047.api.module;

import com.github.grzegorz2047.openguild.module.HardcoreModule;
import java.util.UUID;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.database.SQLHandler;

public class OpenHardcoreModule implements HardcoreModule {
    
    @Override
    public long getBan(UUID uuid) {
        return SQLHandler.getBan(uuid);
    }
    
    @Override
    public boolean isEnabled() {
        return GenConf.hcBans;
    }
    
    @Override
    public void setBan(UUID uuid, long to) {
        SQLHandler.update(uuid, SQLHandler.PType.BAN_TIME, to);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        GenConf.hcBans = enabled;
    }
    
}
