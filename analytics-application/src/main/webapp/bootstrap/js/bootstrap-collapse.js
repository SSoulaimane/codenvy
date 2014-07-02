/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */

!function ($) {

    "use strict"; // jshint ;_;


    /* COLLAPSE PUBLIC CLASS DEFINITION
     * ================================ */

    var Collapse = function (element, options) {
        this.$element = $(element)
        this.options = $.extend({}, $.fn.collapse.defaults, options)

        if (this.options.parent) {
            this.$parent = $(this.options.parent)
        }

        this.options.toggle && this.toggle()
    }

    Collapse.prototype = {

        constructor: Collapse, dimension: function () {
            var hasWidth = this.$element.hasClass('width')
            return hasWidth ? 'width' : 'height'
        }, show: function () {
            var dimension
                , scroll
                , actives
                , hasData

            if (this.transitioning) return

            dimension = this.dimension()
            scroll = $.camelCase(['scroll', dimension].join('-'))
            actives = this.$parent && this.$parent.find('> .accordion-group > .in')

            if (actives && actives.length) {
                hasData = actives.data('collapse')
                if (hasData && hasData.transitioning) return
                actives.collapse('hide')
                hasData || actives.data('collapse', null)
            }

            this.$element[dimension](0)
            this.transition('addClass', $.Event('show'), 'shown')
            this.$element[dimension](this.$element[0][scroll])
        }, hide: function () {
            var dimension
            if (this.transitioning) return
            dimension = this.dimension()
            this.reset(this.$element[dimension]())
            this.transition('removeClass', $.Event('hide'), 'hidden')
            this.$element[dimension](0)
        }, reset: function (size) {
            var dimension = this.dimension()

            this.$element
                .removeClass('collapse')
                [dimension](size || 'auto')
                [0].offsetWidth

            this.$element[size !== null ? 'addClass' : 'removeClass']('collapse')

            return this
        }, transition: function (method, startEvent, completeEvent) {
            var that = this
                , complete = function () {
                    if (startEvent.type == 'show') that.reset()
                    that.transitioning = 0
                    that.$element.trigger(completeEvent)
                }

            this.$element.trigger(startEvent)

            if (startEvent.isDefaultPrevented()) return

            this.transitioning = 1

            this.$element[method]('in')

            $.support.transition && this.$element.hasClass('collapse') ?
                this.$element.one($.support.transition.end, complete) :
                complete()
        }, toggle: function () {
            this[this.$element.hasClass('in') ? 'hide' : 'show']()
        }

    }


    /* COLLAPSIBLE PLUGIN DEFINITION
     * ============================== */

    $.fn.collapse = function (option) {
        return this.each(function () {
            var $this = $(this)
                , data = $this.data('collapse')
                , options = typeof option == 'object' && option
            if (!data) $this.data('collapse', (data = new Collapse(this, options)))
            if (typeof option == 'string') data[option]()
        })
    }

    $.fn.collapse.defaults = {
        toggle: true
    }

    $.fn.collapse.Constructor = Collapse


    /* COLLAPSIBLE DATA-API
     * ==================== */

    $(function () {
        $('body').on('click.collapse.data-api', '[data-toggle=collapse]', function (e) {
            var $this = $(this), href
                , target = $this.attr('data-target')
                    || e.preventDefault()
                    || (href = $this.attr('href')) && href.replace(/.*(?=#[^\s]+$)/, '') //strip for ie7
                , option = $(target).data('collapse') ? 'toggle' : $this.data()
            $(target).collapse(option)
        })
    })

}(window.jQuery);