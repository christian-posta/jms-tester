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
module Fuse
  
  class IncludeThumbs
    
    include Webgen::Tag::Base
    include Webgen::WebsiteAccess
    
    def call(tag, body, context)
      dirname = param('fuse.includethumbs.dirname')
      extension = param('fuse.includethumbs.extension')
      thumbName = param('fuse.includethumbs.thumb')
      
      srcDirectory = File.join(website.directory, "src")
      
      content = []
      
      dest_node = context.ref_node.resolve(dirname)
      
      imgDirectory = File.join(srcDirectory, dest_node.path) 
      searchString = imgDirectory + "/*" + thumbName + extension
      
      Dir[searchString].each{|file|         
        thumbFileName = File.basename(file)
        picFileName = thumbFileName.sub("-" + thumbName, "")
        
        linkText = '<a href="' + dirname + "/" + picFileName + '">'
        linkText += '<img src="' + dirname + "/" + thumbFileName  + '" alt="' + picFileName.sub(extension, "") + '"/>'
        linkText += '</a>'        
        
        content.push(linkText)
      }
      [content]
    end
  end
end
