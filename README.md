# opencv_helper

This is a flutter plugin for opencv. However, the OpenCV library contains too many things, I'm not intend to build a plugin which supports lots of them, and this plugin is mainly for some specific scenarios in another project [Okapia](https://github.com/drriguz/ben/tree/master/okapia). Here are some ideas behind creating this plugin:

* Both android/ios must be supported(that's the biggest benefit of flutter, right?)
* Provide a scaffold for users to build whatever they wanted by using OpenCV, thus it's better to use it in a native c++ way

## Getting Started

This project contains large files(the opencv lib), and you need to manually update your `Podfile`, add a few lines to change the prefix header, otherwise will build fail in IOS:

```ruby
post_install do |installer|
  installer.pods_project.targets.each do |target|
    target.build_configurations.each do |config|
      config.build_settings['ENABLE_BITCODE'] = 'NO'
    end
  end

  # Add the following:
  opencv_prefix = installer.pod_targets.find{|e| e.pod_name == 'opencv_helper' }.prefix_header_path
  puts "updating opencv prefix file #{opencv_prefix} ..."

  originalText = File.read(opencv_prefix)
  new_text =
  <<~EOS
    #ifdef __cplusplus
    #include <opencv2/opencv.hpp>
    #endif

    #{originalText}
  EOS

  File.open(opencv_prefix, "w") {|file| file.puts new_text }
end
  ```
