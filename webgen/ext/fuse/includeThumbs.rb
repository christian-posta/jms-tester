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
        linkText += '<img src="' + dirname + "/" + thumbFileName  + '"/>'
        linkText += '</a>'        
        
        content.push(linkText)
      }
      [content]
    end
  end
end
