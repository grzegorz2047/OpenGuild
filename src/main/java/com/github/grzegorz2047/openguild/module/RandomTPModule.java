/*
 * Copyright 2014
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.grzegorz2047.openguild.module;

import com.github.grzegorz2047.openguild.User;
import javax.annotation.Nonnull;
import org.bukkit.entity.Player;

public interface RandomTPModule {

    boolean isEnabled();

    boolean isButtonEnabled();

    @Deprecated
    void setEnabled(boolean enabled);

    void setButtonEnabled(boolean enabled);

    void teleport(@Nonnull Player player);

    void teleport(@Nonnull User user);

}
