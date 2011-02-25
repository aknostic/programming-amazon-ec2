module Kulitzer
  module CloudFrontImageTag

    S3_PREFIX = 'http://s3.amazonaws.com/media.kulitzer.com'
    CF_PREFIX = 'http://media%d.kulitzer.com'

    def image_tag(source, options = {})
      # For production, re-write url to use CloudFront
      if Rails.env.include?('production') && source.starts_with?(S3_PREFIX)
        # re-write to use a cloudfront url eg
        # http://s3.amazonaws.com/media.kulitzer.com/foo.jpg becomes
        # http://mediaZ.kulitzer.com/foo.jpg where Z = 0..3

        cf_host = CF_PREFIX % rand(4)
        source = source.sub(S3_PREFIX, cf_host)
      end

      super
    end
  end
end

ActionView::Base.send :include, Kulitzer::CloudFrontImageTag
