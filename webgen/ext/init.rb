# Copyright (C) 2009, Progress Software Corporation and/or its
# subsidiaries or affiliates.  All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#     http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# = webgen extensions directory
#
# All init.rb files anywhere under this directory get automatically loaded on a webgen run. This
# allows you to add your own extensions to webgen or to modify webgen's core!
#
# If you don't need this feature you can savely delete this file and the directory in which it is!
#
# The +config+ variable below can be used to access the Webgen::Configuration object for the current
# website.

$LOAD_PATH << File.dirname(__FILE__)
load 'fuse/asciidoc.rb'
load 'fuse/includeThumbs.rb'
#load 'fuse/sitecopy_rake.rb'

config = Webgen::WebsiteAccess.website.config
config['contentprocessor.map']['asciidoc'] = 'Fuse::AsciiDoc'

config['contentprocessor.tags.map']['includethumbs'] = 'Fuse::IncludeThumbs'
config.fuse.includethumbs.dirname(nil, :doc => 'The name of the directory where the thumb files are located.', :mandatory => 'default')
config.fuse.includethumbs.extension('.png', :doc => 'The extension of the thumb files.')
config.fuse.includethumbs.thumb('thumb', :doc => 'Special file basename ending for thumbs.')

module Fuse 
  autoload :AsciiDoc, 'fuse/asciidoc'
  autoload :IncludeThumbs, 'fuse/includeThumbs'
  
  #autoload :SitecopyTask, 'fuse/sitecopy_rake'


end
