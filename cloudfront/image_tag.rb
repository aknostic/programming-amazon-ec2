module Kulitzer
  module CloudFrontImageTag

    S3_MEDIA_PREFIX = 'http://s3.amazonaws.com/media.kulitzer.com'
    CF_MEDIA_PREFIX = 'http://media%d.kulitzer.com'

    def image_tag(source, options = {})
      # For production, re-write url to use CloudFront
      if Rails.env.include?('production') && source.starts_with?(S3_MEDIA_PREFIX)
        # re-write to use a cloudfront url eg
        # http://s3.amazonaws.com/media.kulitzer.com/content/ContestCovers/FOO.jpg becomes
        # http://mediaZ.kulitzer.com/content/ContestCovers/FOO.jpg where Z = 0..3

        cf_host = CF_MEDIA_PREFIX % rand(4)
        source = source.sub(S3_MEDIA_PREFIX, cf_host)
      end

      super
    end
  end
end

ActionView::Base.send :include, Kulitzer::CloudFrontImageTag
